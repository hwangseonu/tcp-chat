package main

import (
	"bufio"
	"io"
	"log"
	"net"
	"sync"
)

const PORT = ":7007"
var socketMap = make(map[string]net.Conn)
var mutex = &sync.Mutex{}

func main() {
	s, err := net.Listen("tcp", PORT)

	if err != nil {
		log.Fatalln(err)
	}

	defer s.Close()

	for {
		conn, err := s.Accept()
		if err != nil {
			log.Fatalln(err)
		}

		go serve(conn)
	}
}

func serve(conn net.Conn) {
	defer conn.Close()

	reader := bufio.NewReader(conn)

	n, _, err := reader.ReadLine()
	name := string(n) + "(" + conn.RemoteAddr().String()+ ")"

	if err != nil {
		log.Fatalln(err)
	}

	mutex.Lock()
	socketMap[name] = conn
	mutex.Unlock()

	broadcast(name + " enter this chat.")
	for {
		line, _, err := reader.ReadLine()
		if err != nil {
			if err == io.EOF {
				broadcast(name + " quit this chat.")
				mutex.Lock()
				delete(socketMap, name)
				mutex.Unlock()
				return
			}
			log.Println(err)
		}
		broadcast(name + ": " + string(line))
	}
}

func broadcast(m string) {
	log.Println(m)

	mutex.Lock()
	for _, conn := range socketMap {
		_, err := conn.Write([]byte(m + "\n"))
		if err != nil {
			log.Println(err)
		}
	}
	mutex.Unlock()
}
