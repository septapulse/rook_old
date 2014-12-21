ROOK: Robotics Orchestration Kit


== Project Goals ==

1. Provide a feature-rich, flexible robotics platform that is easy to use: Rook's main goal is to provide a strong framework that can be utilized by professionals and ametuers alike.

2. Performance and Consistency: Rook strives to provide high-quality performant code. While written in Java, latency-senstive applications will be happy to know that Rook's codebase is largely garbage-free which will help keep GC-pauses at bay.

3. Distributed Computing made easy: As the Internet-of-Things and Advanced Robotics continue to progress, fleets of commodity hardware will provide a wide range of functionality. Therefore, Rook tries to make it easy to distrubute computational power and funtionality between many application instances. All messaging in Rook is done via serialized buffers so that any service written for the platform is automatically supported via networking calls. Expanding on this concept, specialized services are able to connect multiple Rook instances to make them appear as one large application to the developer.
