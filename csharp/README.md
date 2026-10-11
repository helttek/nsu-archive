# C# / .NET — Dining Philosophers

Three implementations of the classic Dining Philosophers problem, each demonstrating a different level of C#/.NET architecture:

- **single_threaded** — deterministic step-by-step simulation on one thread (state machines, events, strategy pattern)
- **multithreaded_simulation** — concurrent simulation with a dedicated thread per philosopher (threading, locks, cancellation, deadlock detection)
- **generic_host** — production-style version on the .NET Generic Host (DI, hosted services, options, logging)

All three are configured via `appsettings.json` and print live state and metrics (meals, fork usage, time spent hungry) to the console.
