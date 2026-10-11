# Single-threaded Dining Philosophers

Deterministic, step-by-step simulation of the Dining Philosophers problem running on a single thread — a showcase of OOP and clean architecture basics.

- Philosophers as state machines (thinking → hungry → eating) driven by commands (`Tick`, `TakeLeft`, `TakeRight`)
- Strategy pattern for fork-picking order (`AlwaysRight`)
- Optional `Coordinator` that drives all philosophers via events and flags a deadlock when everyone is hungry and stuck
- Configuration from `appsettings.json` (Microsoft.Extensions.Configuration)
- Per-step console state output and metrics
