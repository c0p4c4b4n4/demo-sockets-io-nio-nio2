# [draft] Java sockets I/O: blocking, non-blocking and asynchronous


## Introduction

When describing I/O, the terms _non-blocking_ and _asynchronous_ are often used interchangeably, but actually there is a significant difference between them. This article will describe the theoretical and practical differences between non-blocking and asynchronous sockets I/O operations in Java.

Sockets are endpoints to perform communication by TCP and UDP protocols. Java classes, related to sockets, are facades for the corresponding functionality of the operating systems. Actually, socket communication in POSIX-compliant operating systems (Unix, Linux, Mac OS X, BSD, Solaris, AIX, etc.) is performed by _Berkley sockets_ that are actually the standard for the POSIX specification. Socket communication in Windows is performed by _Winsock_ that is also based on _Berkley sockets_ with additional functionality to comply with the Windows programming model.


## The POSIX definitions

In the article are used the simplified definition from _the POSIX specification_:

_Blocked thread_ - a thread that is waiting for some condition to be satisfied before it can continue execution.

_Blocking_ - a property of a socket that causes function calls associated with it to wait for the requested action to be performed before returning.

_Non-blocking_ - a property of a socket that causes function calls involving it to return without delay when it is detected that the requested action associated with the function call cannot be completed without an unknown delay.

_Synchronous I/O operation_ - an I/O operation that causes the thread requesting the I/O to be blocked from further use of the processor until that I/O operation completes.

_Asynchronous I/O operation_ - an I/O operation that does not of itself cause the thread requesting the I/O to be blocked from further use of the processor. This implies that the process and the I/O operation may be running concurrently.


## I/O models

The following I/O models are the most common for the POSIX-compliant operating systems:



*   blocking I/O
*   nonblocking I/O
*   I/O multiplexing
*   signal-driven I/O
*   asynchronous I/O


### Blocking I/O model

In the blocking I/O model, the application makes a blocking system call until data is received at the kernel _and_ is copied from kernel-space buffer to user-space buffer.

![blocking I/O model](/.images/blocking_IO_model.png)

Pros:



*   The simplest I/O model to implement

Cons:



*   The thread is blocked so its task can’t perform other activities during the waiting time


### Nonblocking I/O model

In the non-blocking I/O model the application makes a system call that immediately returns one of two responses:



*   if the I/O operation can be completed immediately, the data is returned
*   if the I/O operation can’t be completed immediately, an error code is returned indicating that the  I/O operation would block or the device is temporarily unavailable

To complete the I/O operation, the application should make repeating calls until completion. 

![non-blocking I/O model](/.images/non_blocking_IO_model.png)

Pros:



*   The thread isn’t blocked and can perform other activities during the waiting time

Cons:



*   The application must busy-wait until the data is available, that would cause many user-kernel context switches
*   This model can introduce latency in the I/O because there can be a gap between the data becoming available in the kernel and the user calling read to return it


### I/O multiplexing model

In the _I/O multiplexing model_ (also known as the _non-blocking I/O model with blocking notifications_), the application makes the first blocking _select_ system call to start to monitor activity for one or many sockets. For each socket, it’s possible to request notification of its readiness for  certain I/O operations (connection, availability for read and write, error occurrence, etc.)

When the blocking _select_ system call returns that at least one socket is ready, the application makes the second _non-blocking_ call and copies the data from kernel-space buffer to user-space buffer.

![I/O multiplexing model](/.images/IO_multiplexing_model.png)

Pros:



*   It’s possible to perform I/O operations on multiple sockets in one thread

Cons:



*   In some operating systems the _select_ system call may be implemented in the way when processing delay increases linearly with the number of monitored sockets


### Signal-driven I/O model

In the signal-driven I/O model the application makes the first non-bloking call and registeres a event handler. When a soket is read to be read or written, an event is generated for the application. Then the event handler copies the data from kernel-space buffer to user-space buffer.

Events can be implemented either by a signal or by a thread-based callback.

![signal-driven I/O model](/.images/signal_driven_IO_model.png)

Pros:



*   The caller isn’t blocked and can perform other activities during that time
*   Event handler based on signals can provide good performance

Cons:



*   Not all operating systems support signals


### Asynchronous I/O model

In the asynchronous I/O model (also known as the overlapped I/O model) the application makes the non-blocking call and starts a background operation in the kernel. When the operation is completed (data is received at the kernel _and_ is copied from kernel-space buffer to user-space buffer), a signal or a thread-based callback is generated to finish the I/O operation. 

The main difference between this model and the signal-driven I/O model is that with signal-driven I/O, the kernel tells us when an I/O operation can be initiated, but with asynchronous I/O, the kernel tells us when an I/O operation is complete.

![asynchronous I/O model](/.images/asynchronous_IO_model.png)

Pros:



*   This model can offer the best scalability and performance

Cons:



*   The most complicated I/O model to implement
*   Not all operating systems support this model efficiently


### Comparison of the I/O models

There are two distinct phases for an I/O operation:



1. waiting for the data to arrive on the network _and_ copying the data into kernel-space buffer
2. copying the data from kernel-space buffer into user-space buffer

The main difference between the first four models is the first phase, as the second phase in the first four models is the same: the process is blocked in a system call while the data is copied from the kernel buffer to the user buffer. Asynchronous I/O, however, handles both phases and is different from the first four.

POSIX defines these two terms as follows:



*   a synchronous I/O operation causes the requesting process to be blocked until that I/O operation completes.
*   an asynchronous I/O operation does not cause the requesting process to be blocked.

Using these definitions, the first four I/O models—blocking, nonblocking, I/O multiplexing, and signal-driven I/O—are all synchronous because the actual I/O operation blocks the thread. Only the asynchronous I/O model matches the asynchronous I/O definition.


## Socket echo server

In the practical part of the article, most of I/O models considered above will be used to implement in Java echo clients and servers based on TCP sockets.

Java supports three I/O API:



*   IO (based on InputStream/OutputStream)
*   NIO  (based on buffers, channels, selectors)
*   NIO2 


### Blocking IO echo server

In the following example, the blocking I/O model is implemented in the echo server with the Java IO API. The accept method of the ServerSocket class, the read method of the InputStream interface, the write method of the OutputStream interface are all blocking.


```
public class IoEchoServer extends Demo {

   public static void main(String[] args) throws IOException {
       ServerSocket serverSocket = new ServerSocket(7000);

       while (active) {
           Socket socket = serverSocket.accept(); // blocking

           InputStream is = socket.getInputStream();
           OutputStream os = socket.getOutputStream();

           int read;
           byte[] bytes = new byte[1024];
           while ((read = is.read(bytes)) != -1) { // blocking
               os.write(bytes, 0, read); // blocking
           }

           socket.close();
       }

       serverSocket.close();
   }
}
```



### Blocking NIO echo server

In the following example, the blocking I/O model is implemented in the echo server with the Java NIO API. The ServerSocketChannel and SocketChannel implicitly are configured in the blocking mode, so the accept method of the ServerSocketChannel object and the read and write methods of the ServerSocket object are blocking.


```
public class NioBlockingEchoServer extends Demo {

   public static void main(String[] args) throws IOException {
       ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
       serverSocketChannel.bind(new InetSocketAddress("localhost", 7000));

       while (active) {
           SocketChannel socketChannel = serverSocketChannel.accept(); // blocking

           ByteBuffer buffer = ByteBuffer.allocate(1024);
           while (true) {
               buffer.clear();
               int read = socketChannel.read(buffer); // blocking
               if (read < 0) {
                   break;
               }

               buffer.flip();
               socketChannel.write(buffer); // blocking
           }

           socketChannel.close();
       }

       serverSocketChannel.close();
   }
}
```



### Non-blocking NIO echo server

In the following example, the non-blocking I/O model is implemented in the echo server with the Java NIO API. The ServerSocketChannel and SocketChannel explicitly are configured in the non-blocking mode, so the accept method of the ServerSocketChannel object and the read and write methods of the ServerSocket object are non-blocking.


```
public class NioNonBlockingEchoServer extends Demo {

   public static void main(String[] args) throws IOException {
       ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
       serverSocketChannel.configureBlocking(false);
       serverSocketChannel.bind(new InetSocketAddress(7000));

       while (active) {
           SocketChannel socketChannel = serverSocketChannel.accept(); // non-blocking
           if (socketChannel != null) {
               socketChannel.configureBlocking(false);

               ByteBuffer buffer = ByteBuffer.allocate(4);
               while (true) {
                   buffer.clear();
                   int read = socketChannel.read(buffer); // non-blocking
                   if (read < 0) {
                       break;
                   }

                   buffer.flip();
                   socketChannel.write(buffer); // non-blocking
               }

               socketChannel.close();
           }
       }

       serverSocketChannel.close();
   }
}
```



### Multiplexing NIO echo server

In the following example, the multiplexing I/O model is implemented in the echo server with the Java NIO API. The ServerSocketChannel and SocketChannel explicitly are configured in the non-blocking mode. In this example is used a Selector as a multiplexer for many ServerSocketChannel objects. Connection accept, socket read and write are all non-blocking and single-threaded. Hoewer, the method Selector.select is blocked.


```
public class NioMultiplexingEchoServer extends Demo {

   public static void main(String[] args) throws IOException {
       final int ports = 7;
       ServerSocketChannel[] serverSocketChannels = new ServerSocketChannel[ports];

       Selector selector = Selector.open();

       for (int p = 0; p < ports; p++) {
           ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
           serverSocketChannels[p] = serverSocketChannel;
           serverSocketChannel.configureBlocking(false);
           serverSocketChannel.bind(new InetSocketAddress("localhost", 7000 + p));

           serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
       }

       while (active) {
           selector.select(); // blocking

           Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
           while (keysIterator.hasNext()) {
               SelectionKey key = keysIterator.next();

               if (key.isAcceptable()) {
                   accept(selector, key);
               }
               if (key.isReadable()) {
                   keysIterator.remove();
                   read(selector, key);
               }
               if (key.isWritable()) {
                   keysIterator.remove();
                   write(key);
               }
           }
       }

       for (ServerSocketChannel serverSocketChannel : serverSocketChannels) {
           serverSocketChannel.close();
       }
   }

   private static void accept(Selector selector, SelectionKey key) throws IOException {
       ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
       SocketChannel socketChannel = serverSocketChannel.accept();
       if (socketChannel != null) {
           socketChannel.configureBlocking(false);
           socketChannel.register(selector, SelectionKey.OP_READ);
       }
   }

   private static void read(Selector selector, SelectionKey key) throws IOException {
       SocketChannel socketChannel = (SocketChannel) key.channel();

       ByteBuffer buffer = ByteBuffer.allocate(4);
       socketChannel.read(buffer); // non-blocking

       buffer.flip();
       socketChannel.register(selector, SelectionKey.OP_WRITE, buffer);
   }

   private static void write(SelectionKey key) throws IOException {
       SocketChannel socketChannel = (SocketChannel) key.channel();
       ByteBuffer buffer = (ByteBuffer) key.attachment();
       socketChannel.write(buffer); // non-blocking
       socketChannel.close();
   }
}
```



### Asynchronous NIO2 echo server

In the following example, the asynchronous I/O model is implemented in the echo server with the Java NIO2 API. The AsynchronousServerSocketChannel, AsynchronousSocketChannel classes are used with completion handlers API, which implies that asynchronous operation is performed implicitly. 

On Windows, asynchronous I/O is implemented with Win32 I/O Completion Ports. On at least some POSIX system asynchronous I/O is implemented with Java thread pools.


```
public class Nio2CompletionHandlerEchoServer extends Demo {

   public static void main(String[] args) throws IOException {
       AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(7000));
       logger.info("echo server started");

       AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(serverSocketChannel);
       serverSocketChannel.accept(null, acceptCompletionHandler);

       System.in.read();
       logger.info("echo server finished");
   }
}

class AcceptCompletionHandler extends Demo implements CompletionHandler<AsynchronousSocketChannel, Void> {

   private final AsynchronousServerSocketChannel serverSocketChannel;

   AcceptCompletionHandler(AsynchronousServerSocketChannel serverSocketChannel) {
       this.serverSocketChannel = serverSocketChannel;
   }

   @Override
   public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
       logger.info("connection accepted: {}", socketChannel);

       serverSocketChannel.accept(null, this);

       ByteBuffer buffer = ByteBuffer.allocate(1024);
       ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(socketChannel, buffer);
       socketChannel.read(buffer, null, readCompletionHandler);
   }

   @Override
   public void failed(Throwable t, Void attachment) {
       logger.error("exception during connection accepting", t);
   }
}

class ReadCompletionHandler extends Demo implements CompletionHandler<Integer, Void> {

   private final AsynchronousSocketChannel socketChannel;
   private final ByteBuffer buffer;

   ReadCompletionHandler(AsynchronousSocketChannel socketChannel, ByteBuffer buffer) {
       this.socketChannel = socketChannel;
       this.buffer = buffer;
   }

   @Override
   public void completed(Integer bytesRead, Void attachment) {
       logger.info("echo server read: {} byte(s)", bytesRead);

       buffer.flip();
       byte[] bytes = new byte[buffer.limit()];
       buffer.get(bytes);
       String message = new String(bytes, StandardCharsets.UTF_8);
       logger.info("echo server received: {}", message);

       WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
       buffer.flip();
       socketChannel.write(buffer, null, writeCompletionHandler);
   }

   @Override
   public void failed(Throwable t, Void attachment) {
       logger.error("exception during socket reading", t);
   }
}

class WriteCompletionHandler extends Demo implements CompletionHandler<Integer, Void> {

   private final AsynchronousSocketChannel socketChannel;

   WriteCompletionHandler(AsynchronousSocketChannel socketChannel) {
       this.socketChannel = socketChannel;
   }

   @Override
   public void completed(Integer bytesWritten, Void attachment) {
       logger.info("echo server wrote: {} byte(s)", bytesWritten);

       try {
           socketChannel.close();
           logger.info("connection closed");
       } catch (IOException e) {
           logger.error("exception during socket closing", e);
       }
   }

   @Override
   public void failed(Throwable t, Void attachment) {
       logger.error("exception during socket writing", t);
   }
}
```


There also a future API, which implies that asynchronous operation is performed explicitly on a thread pool.


## Conclusion

Not always asynchronous I/O implementation is better than others.

The choice of I/O model for sockets communication depends on the parameters of the traffic. If I/O requests are long and infrequent, asynchronous I/O is generally a good way to optimize processing efficiency. However, for relatively fast I/O operations, the overhead of processing kernel I/O requests and kernel signals may make asynchronous I/O less beneficial, particularly if many fast I/O operations need to be made. In this case, synchronous I/O would be better. 

Despite that Java provides unified access to sockets in the supported operating systems, the actual performance can vary significantly depending on their implementation of sockets. It’s possible to start studying the difference with the well-known article [The C10K problem](http://www.kegel.com/c10k.html). Also will lep the following classic books:



*   UNIX Network Programming, volume 1 The Sockets Networking API, 3rd edition
*   Windows Internals, parts 2, 6th edition

Code examples are available in the [GitHub repository](https://github.com/aliakh/demo-sockets-io-nio-nio2).
