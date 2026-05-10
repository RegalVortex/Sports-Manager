# Sports Manager

CE216 course project - Doc. Dr. Kaya Oguz

## Team WW3

| Member | Student ID |
|--------|------------|
| Cuma Parcalar | 20220602063 |
| Ilker Gedik | 20230602026 |
| Oguz Kagan Tekin | 20240602067 |

## Current Status

The project now includes a console-based career mode for managing football and
volleyball teams. The console flow lets the player create or load a career,
choose a sport, select a league and team, manage squad and lineup decisions,
change tactics, simulate weekly fixtures, follow standings, read news, and save
or load progress from local save slots.

Core systems currently covered:

- Football and volleyball sport modules
- League presets and fixture generation
- Match simulation with results, form changes, and injuries
- Team standings, season reset, and champion detection
- Console dashboard and screen-based navigation
- Three local save slots
- JUnit 5 test suite with Maven
- JaCoCo coverage report generation
- Checkstyle report generation

## Requirements

- Java 11+
- Maven 3.8+

## How to Run

Run the console career mode:

```bash
mvn exec:java
```

Run all tests:

```bash
mvn test
```

Generate a Checkstyle report:

```bash
mvn checkstyle:checkstyle
```

## Project Notes

The main entry point is `com.sportsmanager.Main`, which delegates to the console
UI in `com.sportsmanager.ui.console.ConsoleApp`.

JavaFX classes are still present in the project, but the current runnable flow
is the console career mode. CI compiles the full project and excludes JavaFX
scene tests in headless environments.
