# Dining Philosophers on the .NET Generic Host

Production-style Dining Philosophers simulation built on the .NET Generic Host, with each philosopher running as a hosted background service — the architecture used in real .NET services.

- `IHostedService` / `BackgroundService` for each philosopher
- Dependency injection, options pattern (`IOptions<T>`) and `appsettings.json` configuration
- Async fork acquisition with timeouts and cooperative cancellation (`async`/`await`, `CancellationToken`)
- Structured logging (`ILogger`), plus hosted status-printer and deadlock-detector services
- Strategy pattern in a separate project, with strategies selected per philosopher
