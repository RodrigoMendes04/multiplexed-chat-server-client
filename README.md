# 💬 Multiplexed Chat Server & Client in Java  

This repository contains the implementation of a **multiplexed chat server** and a **simple chat client with a graphical interface**, developed in **Java** as part of the **Computer Networks** course at the **FCUP**.  

---

## 📚 Project Description  

This project implements:  
- A **multiplexed chat server (ChatServer.java)** that accepts multiple simultaneous client connections and handles communication using a custom text-line based protocol.  
- A **chat client (ChatClient.java)**, built on a provided GUI skeleton, completed with protocol implementation and capable of:  
  - Sending commands and text messages to the server  
  - Receiving messages from the server in real time on a separate thread, without blocking the UI  

---

## 🧩 Key Features  

- **Server (ChatServer)**:  
  - Uses multiplexing (NIO Selector) to handle multiple clients concurrently  
  - Processes partial and multiple messages via per-client buffering  
  - Supports the following commands from clients:  
    - `/nick name` — Set or change nickname  
    - `/join room` — Join or create a chat room  
    - `/leave` — Leave the current chat room  
    - `/bye` — Disconnect from the server  
    - `/priv name message` *(optional feature)* — Send a private message to another user  

- **Client (ChatClient)**:  
  - Connects to the server using DNS name and TCP port  
  - Accepts user commands and text input via a simple GUI  
  - Uses two threads: one for sending input, and another for listening to server messages  
  - Processes and displays server messages in a user-friendly format, such as:  
    - `username: message` for chat messages  
    - `username has changed their name to new_name` for nickname changes  
    - `username joined the room` and `username left the room` for join/leave notifications  

---

## 💻 Command Line Usage  

### ✅ Compile:  
```bash
javac ChatServer.java ChatClient.java
```
### ✅ Starting the Server:  
```bash
java ChatServer 8000
```
### ✅ Starting the Client:
```bash
java ChatClient localhost 8000
```
