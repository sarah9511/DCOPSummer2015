all:
	javac -cp "./lib/akka-actor_2.11-2.3.9.jar:./lib/config-1.2.1.jar:./lib/protobuf-java-2.5.0.jar:./lib/akka-remote_2.11-2.3.9.jar:./lib/netty-3.8.0.Final.jar:./lib/scala-library-2.11.5.jar:./lib/mysql-connector-java-5.1.37-bin.jar" -d bin src/smartgrids/message/*.java src/smartgrids/*.java
run:
	top -b -n 60 | grep java > usage.txt &
	java -cp "./lib/akka-actor_2.11-2.3.9.jar:./lib/config-1.2.1.jar:./lib/protobuf-java-2.5.0.jar:./lib/akka-remote_2.11-2.3.9.jar:./lib/netty-3.8.0.Final.jar:./lib/scala-library-2.11.5.jar:./bin:./test" smartgrids.AgentGenerator test/agents/agt1.xml > out.txt 
run2:
	java -cp "./lib/akka-actor_2.11-2.3.9.jar:./lib/config-1.2.1.jar:./lib/protobuf-java-2.5.0.jar:./lib/akka-remote_2.11-2.3.9.jar:./lib/netty-3.8.0.Final.jar:./lib/scala-library-2.11.5.jar:./lib/mysql-connector-java-5.1.37-bin.jar:./bin:./test" smartgrids.AgentGenerator test/agents/dom10/a_0.xml 
