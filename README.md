# [Draft] Java sockets I/O: blocking, non-blocking and asynchronous


## Introduction

When describing I/O, the terms _non-blocking_ and _asynchronous_ are often used interchangeably, but there is actually a significant difference between them. In this article is described the theoretical and practical differences between non-blocking and asynchronous sockets I/O operations in Java.

Sockets are endpoints to perform two-way communication by TCP and UDP protocols. Actually, Java sockets APIs are adapters for the corresponding functionality of the operating systems. Sockets communication in POSIX-compliant operating systems (Unix, Linux, Mac OS X, BSD, Solaris, AIX, etc.) is performed by _Berkeley sockets_. Sockets communication in Windows is performed by _Winsock_ that is also based on _Berkeley sockets_ with additional functionality to comply with the Windows programming model.


## The POSIX definitions

In the article are used the simplified definitions from the POSIX specification:

_Blocked thread_ - a thread that is waiting for some condition before it can continue execution.

_Blocking_ - a property of a socket that causes function calls to it to wait for the requested action to be performed before returning.

_Non-blocking_ - a property of a socket that causes function calls to it to return without delay, when it is detected that the requested action cannot be completed without an unknown delay.

_Synchronous I/O operation_ - an I/O operation that causes the requesting thread to be blocked until that I/O operation completes.

_Asynchronous I/O operation_ - an I/O operation that doesn’t of itself cause the requesting thread to be blocked; this implies that the thread and the I/O operation may be running concurrently.

So, according to the POSIX specification, the difference between the terms _non-blocking_ and _asynchronous_ is straightforward:



*   _non-blocking_ - a property of a socket that causes function calls to it in some conditions to return without delay
*   _asynchronous I/O_ - a property on an I/O operation (reading or writing) that runs concurrently with the requesting thread


## I/O models

The following I/O models are the most common for the POSIX-compliant operating systems:



*   blocking I/O model
*   nonblocking I/O model
*   I/O multiplexing model
*   signal-driven I/O model
*   asynchronous I/O model


### Blocking I/O model

In the _blocking I/O model_, the application makes a blocking system call until data is received at the kernel _and_ is copied from kernel-space buffer to user-space buffer.

![blocking I/O model](/.images/blocking_IO_model.png)

Pros:



*   The simplest I/O model to implement

Cons:



*   The application is blocked


### Nonblocking I/O model

In the _non-blocking I/O model_ the application makes a system call that immediately returns one of two responses:



*   if the I/O operation can be completed immediately, the data is returned
*   if the I/O operation can’t be completed immediately, an error code is returned indicating that the  I/O operation would block or the device is temporarily unavailable

To complete the I/O operation, the application should make repeating system calls until completion. 

![non-blocking I/O model](/.images/non_blocking_IO_model.png)

Pros:



*   The application isn’t blocked

Cons:



*   The application must busy-wait until the data is available, that would cause many user-kernel context switches
*   This model can introduce latency in the I/O because there can be a gap between the data becoming available in the kernel and the user calling read to return it


### I/O multiplexing model

In the _I/O multiplexing model_ (also known as the _non-blocking I/O model with blocking notifications_), the application makes the first _blocking_ _select_ system call to start to monitor activity on many sockets. For each socket, it’s possible to request notification of its readiness for certain I/O operations (connection, availability for reading and writing, error occurrence, etc.)

When the blocking _select_ system call returns that at least one socket is ready, the application makes the second _non-blocking_ call and copies the data from kernel-space buffer to user-space buffer.

![I/O multiplexing model](/.images/IO_multiplexing_model.png)

Pros:



*   It’s possible to perform I/O operations on multiple sockets in one thread

Cons:



*   The application is still blocked on the _select_ system call
*   In some operating systems the _select_ system call may be implemented in the way when processing delay increases linearly with the number of monitored sockets


### Signal-driven I/O model

In the signal-driven I/O model the application makes the first non-blocking call and registers an event handler. When a socket is read to be read or written, an event is generated for the application. Then the event handler copies the data from kernel-space buffer to user-space buffer.

Events can be implemented either by a signal or by a thread-based callback.

![signal-driven I/O model](/.images/signal_driven_IO_model.png)

Pros:



*   The application isn’t blocked
*   Event handler based on signals can provide good performance

Cons:



*   Not all operating systems support signals


### Asynchronous I/O model

In the _asynchronous I/O model_ (also known as the _overlapped I/O model_) the application makes the non-blocking call and starts a background operation in the kernel. When the operation is completed (data is received at the kernel _and_ is copied from kernel-space buffer to user-space buffer), a signal or a thread-based callback is generated to finish the I/O operation. 

The main difference between this model and the signal-driven I/O model is that with signal-driven I/O, the kernel tells the application when an I/O operation _can be initiated_, but with this model, the kernel tells the application when an I/O operation _is complete_.

![asynchronous I/O model](/.images/asynchronous_IO_model.png)

Pros:



*   The application isn’t blocked
*   This model can offer the best scalability and performance

Cons:



*   The most complicated I/O model to implement
*   Not all operating systems support this model efficiently


### Comparison of the I/O models

There are two distinct phases for an I/O operation:



1. waiting for the data to arrive on the network _and_ copying the data into kernel-space buffer
2. copying the data from kernel-space buffer into user-space buffer

The main difference between the first four models is the _first_ phase, as the _second_ phase in the first four models is the same: the thread is blocked on a system call while the data is copied from the kernel buffer to the user buffer. Asynchronous I/O, however, handles both phases and is different from the first four.

The POSIX specification  defines these two terms as follows:



*   a synchronous I/O operation causes the requesting thread to be blocked until that I/O operation completes.
*   an asynchronous I/O operation does not cause the requesting thread to be blocked.

Using these definitions, the first four I/O models - _blocking I/O, nonblocking I/O, I/O multiplexing, signal-driven I/O_ - are all synchronous because the  I/O operation blocks the thread. Only the _asynchronous I/O model_ matches the asynchronous I/O definition.


## Java I/O APIs


### IO API

Java IO API is based on streams (_InputStream_, _OutputStream_) that represent blocking, one-directional bytes flow.


### NIO API

Java NIO API is based on the three classes: _Channel_, _Buffer_, _Selector_.

The _Channel_ class represents a connection to an entity (hardware device, file, socket, software component) that is capable of performing I/O operations (reading or writing). 

<sub>In comparison with streams that are uni-directional, channels are bi-directional.</sub>

The _Buffer_ class is a fixed-size data container with additional methods to read and write data. All _Channel_ data is handled through _Buffer_ but never directly: all data that is sent to a _Channel_ must first be placed in a _Buffer_, any data that is read from a _Channel_ is read into a _Buffer_.

<sub>In comparison with streams, that are byte-oriented, channels are block-oriented. Byte-oriented I/O is simpler but for some I/O devices can be rather slow. Block-oriented I/O can be much faster but is more complicated. </sub>

The _Selector_ class allows subscribing to different events from _many_ registered _SelectableChannel_ objects in a _single_ place. When events arrive, a _Selector_ object dispatches them to the corresponding event handlers.


### NIO2 API

Java NIO2 API is based on asynchronous channels (_AsynchronousServerSocketChannel_, _AsynchronousSocketChannel, etc_) that support asynchronous I/O operations (connecting, reading, writing).

The asynchronous channels provide two mechanisms to control asynchronous I/O operations. The first mechanism is by returning a _java.util.concurrent.Future_ object, which models a pending operation and can be used to query its state and obtain the result. The second mechanism is by passing to the operation a _java.nio.channels.CompletionHandler_ object, which defines handler methods that are executed after the operation has completed _or_ failed. Actually, the provided API for both mechanisms are equivalent.

Asynchronous channels provide a standard way of performing asynchronous operations platform-independently. However, the amount that the API can exploit native asynchronous capabilities of an operating system, will depend on the Java support for that platform.


## Socket echo server

In the practical part of the article, most of the I/O models mentioned above are implemented in echo clients and servers with Java sockets APIs. A server listens on a registered TCP port 7000. A client connects from its dynamic TCP port to the server port. The client reads an input string from the console and sends the bytes to the server port. The server receives the bytes from its port and sends them back to the client port. The client writes the echoed string on the console. When the client receives the same number of bytes that it has sent, it disconnects from the server.

<sub>The conversion between strings and bytes is performed in UTF-8 encoding.</sub>


### Blocking IO echo server

In the following example, the _blocking I/O model _is implemented in an echo server with Java IO API. 

The _ServerSocket.accept_ method blocks until a connection is accepted. The _InputStream.read_ method blocks until input data are available, or a client is disconnected. The _OutputStream.write_ method blocks until all output data are written.


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

In the following example, the _blocking I/O model_ is implemented in an echo server with Java NIO API. 

The _ServerSocketChannel_ and _SocketChannel_ objects are by default configured in the blocking mode. The _ServerSocketChannel.accept_ method blocks and returns a SocketChannel object when a connection is accepted. The _ServerSocket.read_ method blocks until input data are available, or a client is disconnected. The _ServerSocket.write_ method blocks until all output data are written.


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

In the following example, the _non-blocking I/O model_ is implemented in an echo server with Java NIO API. 

The _ServerSocketChannel_ and _SocketChannel_ objects are explicitly configured in the non-blocking mode. The _ServerSocketChannel.accept_ method doesn't block and returns _null_ if no connection is accepted or a _SocketChannel_ object otherwise. The _ServerSocket.read_ doesn't block and returns 0 if no data are available or a positive number otherwise. The _ServerSocket.write_ method doesn't block _only if_ there is free space in the socket's output buffer. 


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

In the following example, the _multiplexing I/O model_ is implemented in an echo server Java NIO API. 

During the initialization, multiple _ServerSocketChannel_ objects, that are configured in the non-blocking mode, are registered by the _SelectableChannel.register_ method on the single _Selector_ object. During that the _SelectionKey.OP_ACCEPT_ argument is used to specify that the event of connection acceptance is interesting.

In the main loop, the _Selector.select_ method blocks until at least one of the registered events occurs. Then the _Selector.selectedKeys_ method returns a set of the _SelectionKey_ objects for which events have occurred. Iterating through the _SelectionKey_ objects, it’s possible to determine what I/O event (connect, accept, read, write) has happened and which sockets objects (_ServerSocketChannel, SocketChannel_) have been associated with that event.

When a _SelectionKey_ indicates that the connection acceptance event has happened, it’s made a non-blocking _ServerSocketChannel.accept_ call. After that the created _SocketChannel_ object is configured in the non-blocking mode and is registered on the same _Selector_ object with the _SelectionKey.OP_READ_ argument to specify that now the event of reading is interesting.

<sub>Indication of a selection key that a channel is ready for some operation category is a hint, but not a guarantee.</sub>

When a _SelectionKey_ indicates that the read event has happened, it’s made a non-blocking _SocketChannel.read_ call to read data from the _SocketChannel_ object into a new _ByteByffer_ object. After that, the _SocketChannel_ object is registered on the same Selector object with the _SelectionKey.OP_WRITE_ argument to specify that now the event of write is interesting. Additionally, the _ByteBuffer_ object is used during the registration as an _attachment_.

When a _SelectionKeys_ indicates that the write event has happened, it’s made a non-blocking _SocketChannel.write_ call to write data

data to the _SocketChannel_ object from the _ByteByffer_ object, extracted from the _SelectionKey.attachment()_ method.

After every read and write operation the _SelectionKey_ object is removed from the set of the _SelectionKey_ objects to prevent its reuse. But the _SelectionKey_ object for connection acceptance is not removed to have the ability to make a next connection acceptance.


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
       SocketChannel socketChannel = serverSocketChannel.accept(); // can be non-blocking
       if (socketChannel != null) {
           socketChannel.configureBlocking(false);
           socketChannel.register(selector, SelectionKey.OP_READ);
       }
   }

   private static void read(Selector selector, SelectionKey key) throws IOException {
       SocketChannel socketChannel = (SocketChannel) key.channel();

       ByteBuffer buffer = ByteBuffer.allocate(1024);
       socketChannel.read(buffer); // can be non-blocking

       buffer.flip();
       socketChannel.register(selector, SelectionKey.OP_WRITE, buffer);
   }

   private static void write(SelectionKey key) throws IOException {
       SocketChannel socketChannel = (SocketChannel) key.channel();

       ByteBuffer buffer = (ByteBuffer) key.attachment();

       socketChannel.write(buffer); // can be non-blocking
       socketChannel.close();
   }
}
```



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


When a connection is accepted (or the operation fails), the _AcceptCompletionHandler_ class is called, which by the _AsynchronousSocketChannel.read(ByteBuffer destination, A attachment, CompletionHandler&lt;Integer,? super A> handler)_ method initiates an asynchronous read operation from the _AsynchronousSocketChannel_ object to a new _ByteBuffer_ object.


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


When the read operation completes (or the operation fails), the _ReadCompletionHandler_ class is called, which by the _AsynchronousSocketChannel.write(ByteBuffer source, A attachment, CompletionHandler&lt;Integer,? super A> handler)_ method initiates an asynchronous write operation to the _AsynchronousSocketChannel_ object from the _ByteBuffer_ object.


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


When the write operation completes (or the operation fails), the _WriteCompletionHandler_ class is called, which by the _AsynchronousSocketChannel.close_ method closes the connection.


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

The choice of I/O model for sockets communication depends on the parameters of the traffic. If I/O requests are long and infrequent, asynchronous I/O is generally a good choice. However, if I/O requests are short and fast, the overhead of processing kernel I/O requests and kernel signals may make asynchronous I/O less beneficial. In this case, synchronous I/O would be better. 

Despite that Java provides a standard way of performing sockets I/O in the different operating systems, the actual performance can vary significantly depending on their implementation. It’s possible to start studying the differences with the well-known article [The C10K problem](http://www.kegel.com/c10k.html). 

Complete code examples are available in the [GitHub repository](https://github.com/aliakh/demo-sockets-io-nio-nio2).
