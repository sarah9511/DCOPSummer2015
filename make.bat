javac -cp .\lib\akka-actor_2.11-2.3.9.jar;.\lib\config-1.2.1.jar;.\lib\protobuf-java-2.5.0.jar;.\lib\akka-remote_2.11-2.3.9.jar;.\lib\netty-3.8.0.Final.jar;.\lib\scala-library-2.11.5.jar -d classes src/*.java

java -cp lib\akka-actor_2.11-2.3.9.jar;lib\config-1.2.1.jar;lib\protobuf-java-2.5.0.jar;lib\akka-remote_2.11-2.3.9.jar;lib\netty-3.8.0.Final.jar;lib\scala-library-2.11.5.jar;classes AgentGenerator
