package main

import (
	"bufio"
	"fmt"
	"io"
	"log"
	"net"
	"os"
)

const PORT = ":7007"

func main() {
	if len(os.Args) < 2 {
		fmt.Printf("usage: %s <server ip>\n", os.Args[0])
		os.Exit(-1)
	}

	name := ""
	fmt.Print("Username: ")
	fmt.Scanf("%s", &name)

	conn, err := net.Dial("tcp", os.Args[1] + PORT)
	conn.Write([]byte(name + "\n"))

	if err != nil {
		log.Fatalln(err)
	}

	defer conn.Close()

	go handleSocket(conn)


	for {
		r := bufio.NewReader(os.Stdin)
		fmt.Print("> ")

		if l, _, err := r.ReadLine(); err != nil {
			log.Fatalln(err)
		} else if _, err := conn.Write(append(l, '\n')); err != nil {
			log.Fatalln(err)
		}
	}
}

func handleSocket(conn net.Conn) {
	defer conn.Close()
	reader := bufio.NewReader(conn)
	for {
		line, _, err := reader.ReadLine()
		if err != nil {
			if err == io.EOF {
				return
			}
			log.Fatalln(err)
		}

		if err != nil {
			log.Fatalln(err)
		}
		fmt.Println("\r" + string(line))
		fmt.Print("> ")
	}
}
