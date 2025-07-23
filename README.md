# zio-datastar-poc

This project is a proof-of-concept (POC) demonstrating the integration of Datastar with ZIO HTTP.    
It showcases how to build a simple, real-time, and interactive web application using a modern Scala stack.

The application is a simple counter that can be incremented and decremented, with the UI updated in real-time using Server-Sent Events (SSE) powered by Datastar.

## Features

- **ZIO HTTP Endpoints**: Uses the type-safe Endpoint API from ZIO HTTP.
- **Datastar Integration**: Leverages Datastar for seamless frontend reactivity without writing JavaScript.
- **Scala 3 Macros**: Includes a Scala 3 macro for compile-time-safe extraction of case class field names.
- **sbt Build**: Configured with sbt for dependency management and running the application.

## How to Run

To run the server, you can use the following sbt command:

```bash
sbt start
```

This will start the application with `sbt-revolver`, which also provides hot-reloading.    
Once the server is running, you can access the application at [http://localhost:8080](http://localhost:8080).

## Technology Stack

- [Scala 3](https://www.scala-lang.org/)
- [ZIO](https://zio.dev/)
- [ZIO HTTP](https://zio.dev/zio-http/)
- [Datastar](https://data-star.dev/)
- [sbt](https://www.scala-sbt.org/)
