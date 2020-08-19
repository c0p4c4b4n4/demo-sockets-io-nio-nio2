# [draft] Java sockets I/O: blocking, non-blocking and asynchronous


## Introduction

When describing I/O, the terms _non-blocking_ and _asynchronous_ are often used interchangeably, but there is actually a significant difference between them. In this article is described the theoretical and practical differences between non-blocking and asynchronous sockets I/O operations in Java.

Sockets are endpoints to perform two-way communication by TCP and UDP protocols. Actually, Java sockets APIs are adapters for the corresponding functionality of the operating systems. Sockets communication in POSIX-compliant operating systems (Unix, Linux, Mac OS X, BSD, Solaris, AIX, etc.) is performed by _Berkeley sockets_ that are actually the standard for the POSIX specification. Sockets communication in Windows is performed by _Winsock_ that is also based on _Berkeley sockets_ with additional functionality to comply with the Windows programming model.


## The POSIX definitions

In the article are used the simplified definitions from the POSIX specification:

_Blocked thread_ - a thread that is waiting for some condition before it can continue execution.

_Blocking_ - a property of a socket that causes function calls associated with it to wait for the requested action to be performed before returning.

_Non-blocking_ - a property of a socket that causes function calls involving it to return without delay, when it is detected that the requested action cannot be completed without an unknown delay.

_Synchronous I/O operation_ - an I/O operation that causes the thread requesting the I/O to be blocked until that I/O operation completes.

_Asynchronous I/O operation_ - an I/O operation that doesn’t of itself cause the thread requesting the I/O to be blocked; this implies that the process and the I/O operation may be running concurrently.


## I/O models

The following I/O models are the most common for the POSIX-compliant operating systems:



*   blocking I/O model
*   nonblocking I/O model
*   I/O multiplexing model
*   signal-driven I/O model
*   asynchronous I/O model


### Blocking I/O model

In the blocking I/O model, the application makes a blocking system call until data is received at the kernel _and_ is copied from kernel-space buffer to user-space buffer.

![blocking I/O model](/.images/blocking_IO_model.png)

Pros:



*   The simplest I/O model to implement

Cons:



*   The thread is blocked and can’t perform other activities during the waiting time


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

In the _asynchronous I/O model_ (also known as the _overlapped I/O model_) the application makes the non-blocking call and starts a background operation in the kernel. When the operation is completed (data is received at the kernel _and_ is copied from kernel-space buffer to user-space buffer), a signal or a thread-based callback is generated to finish the I/O operation. 

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


## Java I/O APIs


### IO API

Java IO API is based on streams (_InputStream_, _OutputStream_) that represents one-directional bytes flow.


### NIO API

Java NIO API is based on the three classes: _Channel_, _Buffer_, _Selector_.

The _Channel_ class represents an open connection to an entity (hardware device, file, socket, software component) that is capable of performing I/O operations (reading or writing). 

In comparison with streams (_InputStream, OutputStream_), that are uni-directional, channels are bi-directional.

The _Buffer_ class is a fixed-size data container that additionally has methods to read and write data. All _Channel_ data is handled through _Buffer_ but never directly: all data that is sent to a _Channel_ must first be placed in a Buffer; any data that is read from a _Channel_ is read into a _Buffer_.

In comparison with streams, that are byte-oriented, channels are block-oriented. Byte-oriented I/O is simpler but for some I/O devices can be rather slow. Block-oriented I/O can be much faster, but is more complicated. 

The Selector class is a multiplexor (an entity with _many_ inputs and a _single_ output) that allows to get I/O events from _many_ registered _SelectableChannel_ objects in a _single_ call. 

It demultiplexes incoming client requests and dispatches them to their respective request handlers.  A selector is analogous to a Windows message loop, in which the selector captures the various events from different clients and dispatches them to their respective event handlers.

The Selector multiplexes events on several SelectableChannels. Each Channel registers events with the Selector. When events arrive from clients, the Selector demutliplexes them and dispatches the events to the corresponding Channels.

Each Channel that has to service client requests must next register itself with the Selector. A Channel should be registered according to the events it will handle. 

A Channel's registration with the Selector is represented by a SelectionKey object. 


### NIO2 API

In NIO2 were added asynchronous channels (_AsynchronousServerSocketChannel_, _AsynchronousSocketChannel _etc) that have API similar to channels (_ServerSocketChannel_, _SocketChannel _etc) and additionally have methods for connecting, reading, writing to be executed asynchronously.

The asynchronous channel API provides two mechanisms for controlling the initiated asynchronous I/O operations. The first is by returning a _java.util.concurrent.Future_ object, which models a pending operation and can be used to query its state and obtain the result. The second is by passing to the operation a _java.nio.channels.CompletionHandler_ object, which defines handler methods that are executed after the operation has completed _or_ failed. The provided API for both mechanisms are equivalent.

Asynchronous channel API provides a standard way of performing asynchronous operations platform-independently. However, the amount that the API can exploit native asynchronous capabilities of an operating system, will depend on the Java support for that platform.


## Socket echo server

In the practical part of the article, most of the I/O models considered above are implemented in TCP sockets echo clients and servers. A client read text input from the console and sent it to TCP port 7000. A server receives bytes from the port and sends them back to the client. Then the client writes echo for console. Conversion between strings and bytes is performed in the UTF-8 encoding.


### Blocking IO echo server

In the following example, _the blocking I/O model _is implemented in an echo server with Java IO API. 

The _ServerSocket.accept_ method blocks until a connection is accepted. 

The _InputStream.read_ method blocks until it’s received the whole input buffer or the input stream is closed.

The _OutputStream.write_ method blocks until it’s sent the whole output buffer


```
public class IoEchoServer {

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

In the following example, _the blocking I/O model_ is implemented in an echo server with Java NIO API. 

The _ServerSocketChannel_ and _SocketChannel_ objects are implicitly configured in the blocking mode. The _ServerSocketChannel.accept_ method blocks and returns a SocketChannel object when an incoming connection is accepted.. 

The _ServerSocket.read_ method blocks.

The _ServerSocket.write_ method blocks. 


```
public class NioBlockingEchoServer {

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

In the following example, _the non-blocking I/O model_ is implemented in an echo server with Java NIO API. 

The _ServerSocketChannel_ and _SocketChannel_ objects are explicitly configured in the non-blocking mode. The _ServerSocketChannel.accept_ method doesn't block and returns _null_ if no incoming connection is available or a SocketChannel otherwise. 

The _ServerSocket.read_ doesn't block and returns 0 if no data is available and positive number otherwise.

The _ServerSocket.write_ method doesn't block (if the number of written bytes is less than free size in the output buffer of the socket). 


```
public class NioNonBlockingEchoServer {

   public static void main(String[] args) throws IOException {
       ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
       serverSocketChannel.configureBlocking(false);
       serverSocketChannel.bind(new InetSocketAddress(7000));

       while (active) {
           SocketChannel socketChannel = serverSocketChannel.accept(); // non-blocking
           if (socketChannel != null) {
               socketChannel.configureBlocking(false);

               ByteBuffer buffer = ByteBuffer.allocate(1024);
               while (true) {
                   buffer.clear();
                   int read = socketChannel.read(buffer); // non-blocking
                   if (read < 0) {
                       break;
                   }

                   buffer.flip();
                   socketChannel.write(buffer); // can be non-blocking
               }

               socketChannel.close();
           }
       }

       serverSocketChannel.close();
   }
}
```



### Multiplexing NIO echo server

In the following example, _the multiplexing I/O model_ is implemented in an echo server Java NIO API. 

In this example are used multiple ServerSocketChannel objects. They are configured in the non-blocking mode. The ServerSocketChannel objects are registered on the same Selector object by the SelectableChannel.register method. During that is used the SelectionKey.OP_ACCEPT argument to specify that the event of connection acceptance is interesting.

In the main loop, the Selector.select method is called. This method blocks until at least one of the registered events occurs. Then the Selector.selectedKeys() method returns a Set of the SelectionKey objects for which events have occurred. Iterating through the SelectionKey objects, it’s possible to determine what I/O event (connect, accept, read, write) has happened and which I/O objects (ServerSocketChannel, SocketChannel) have been associated with that event.

When a SelectionKey indicates that the connection acceptance event has happend, it’s made a non-blocking ServerSocketChannel.accept call. After that the created SocketChannel is configured in the non-blocking mode and is registered on the same Selector object with the SelectionKey.OP_READ argument to specify that the event of read is interesting.

When a SelectionKey indicates that the read event has happened, it’s made a non-blocking SocketChannel.read call to read data from SocketChannel into a new ByteByffer. After that the SocketChannel is registered on the same Selector object with the SelectionKey.OP_WRITE argument to specify that the event of write is interesting. Additionally, the ByteBuffer object is used as an _attachment_.

When a SelectionKeys indicates that the write event has happened, it’s made a non-blocking SocketChannel.write call to write data

data to the SocketChannel from the ByteByffer, extracted from the SelectionKey.attachment() method.

After every read and write operation the SelectionKey object is removed from Set of the SelectionKey objects to prevent it from further processing. But the SelectionKey for connection acceptance is not removed to have the ability to make a next incoming connection to the ServerSocketChannel.


```
public class NioMultiplexingEchoServer {

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
       SocketChannel socketChannel = serverSocketChannel.accept(); // non-blocking
       if (socketChannel != null) {
           socketChannel.configureBlocking(false);
           socketChannel.register(selector, SelectionKey.OP_READ);
       }
   }

   private static void read(Selector selector, SelectionKey key) throws IOException {
       SocketChannel socketChannel = (SocketChannel) key.channel();

       ByteBuffer buffer = ByteBuffer.allocate(1024);
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


This example shows that using selectors it’s possible to handle multiple ServerSocketChannel in a single thread. Hoewer, the Selector.select() call is blocking.


### Asynchronous NIO2 echo server

In the following example, the _asynchronous I/O model_ is implemented in an echo server with Java NIO2 API. The _AsynchronousServerSocketChannel_, _AsynchronousSocketChannel_ classes herein are used with the _completion handlers_ mechanism.

The _AsynchronousServerSocketChannel.accept(A attachment, CompletionHandler&lt;AsynchronousSocketChannel,? super A> handler)_ method initiates an asynchronous operation of connection acceptance.


```
public class Nio2CompletionHandlerEchoServer {

   public static void main(String[] args) throws IOException {
       AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(7000));

       AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(serverSocketChannel);
       serverSocketChannel.accept(null, acceptCompletionHandler);

       System.in.read();
   }
}
```


When connection is accepted (or the operation fails), the _AcceptCompletionHandler_ class is called, which by the _AsynchronousSocketChannel.read(ByteBuffer destination, A attachment, CompletionHandler&lt;Integer,? super A> handler)_ method initiates an asynchronous read operation from the AsynchronousSocketChannel to a new ByteBuffer object.


```
class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {

   private final AsynchronousServerSocketChannel serverSocketChannel;

   AcceptCompletionHandler(AsynchronousServerSocketChannel serverSocketChannel) {
       this.serverSocketChannel = serverSocketChannel;
   }

   @Override
   public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
       serverSocketChannel.accept(null, this); // non-blocking

       ByteBuffer buffer = ByteBuffer.allocate(1024);
       ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(socketChannel, buffer);
       socketChannel.read(buffer, null, readCompletionHandler); // non-blocking
   }

   @Override
   public void failed(Throwable t, Void attachment) {
       // exception handling
   }
}
```


When the read operation completes (or the operation fails), the _ReadCompletionHandler_ class is called, which by the _AsynchronousSocketChannel.write(ByteBuffer source, A attachment, CompletionHandler&lt;Integer,? super A> handler)_ method initiates an asynchronous write operation to the AsynchronousSocketChannel from the ByteBuffer object.


```
class ReadCompletionHandler implements CompletionHandler<Integer, Void> {

   private final AsynchronousSocketChannel socketChannel;
   private final ByteBuffer buffer;

   ReadCompletionHandler(AsynchronousSocketChannel socketChannel, ByteBuffer buffer) {
       this.socketChannel = socketChannel;
       this.buffer = buffer;
   }

   @Override
   public void completed(Integer bytesRead, Void attachment) {
       WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
       buffer.flip();
       socketChannel.write(buffer, null, writeCompletionHandler); // non-blocking
   }

   @Override
   public void failed(Throwable t, Void attachment) {
       // exception handling
   }
}
```


When the write operation completes (or the operation fails), the _WriteCompletionHandler_ class is called, which by the _AsynchronousSocketChannel.close()_ method closes the connection.


```
class WriteCompletionHandler implements CompletionHandler<Integer, Void> {

   private final AsynchronousSocketChannel socketChannel;

   WriteCompletionHandler(AsynchronousSocketChannel socketChannel) {
       this.socketChannel = socketChannel;
   }

   @Override
   public void completed(Integer bytesWritten, Void attachment) {
       try {
           socketChannel.close();
       } catch (IOException e) {
           // exception handling
       }
   }

   @Override
   public void failed(Throwable t, Void attachment) {
       // exception handling
   }
}
```


In this example, asynchronous I/O operations are performed without _attachment_, because all the necessary objects (_AsynchronousSocketChannel_,_ ByteBuffer_) are passed as constructor arguments for the appropriate _completion handlers_.


## Conclusion

Not always asynchronous I/O implementation is better than others.

The choice of I/O model for sockets communication depends on the parameters of the traffic. If I/O requests are long and infrequent, asynchronous I/O is generally a good way to optimize processing efficiency. However, for relatively fast I/O operations, the overhead of processing kernel I/O requests and kernel signals may make asynchronous I/O less beneficial, particularly if many fast I/O operations need to be made. In this case, synchronous I/O would be better. 

Despite that Java provides unified access to sockets in the supported operating systems, the actual performance can vary significantly depending on their implementation of sockets. It’s possible to start studying the difference with the well-known article [The C10K problem](http://www.kegel.com/c10k.html). Also will lep the following classic books:



*   UNIX Network Programming, Volume 1 The Sockets Networking API, 3rd edition
*   Windows Internals, Part 2, 6th edition

Code examples are available in the [GitHub repository](https://github.com/aliakh/demo-sockets-io-nio-nio2).
