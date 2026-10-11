# Multithreaded Dining Philosophers

Concurrent simulation of the Dining Philosophers problem with a dedicated thread per philosopher — practical multithreading and synchronization in C#.

- Thread lifecycle management (background threads, cooperative shutdown via `CancellationToken`)
- Lock-based synchronization (`lock`, `volatile`) for shared state
- Configurable fork-acquisition timeout with retry and backoff
- Cycle-waiting deadlock detector that reports the deadlock cycle and stops the simulation
- Live metrics: meals eaten, time spent hungry, fork usage
