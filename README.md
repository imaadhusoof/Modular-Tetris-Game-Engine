# Modular Tetris Game Engine

A Tetris game in Java that our team refactored from a tightly coupled codebase into a modular design, using GRASP principles and GoF design patterns. We then extended it with new piece types and gameplay features. It's built on the JGameGrid library, with Gradle.

This was a three-person team project:

- **Barry Saaun** ([@barry-saaun](https://github.com/barry-saaun))
- **Zane Sekkouah** ([@Wontkins](https://github.com/Wontkins))
- **Imaadh Usoof** ([@imaadhusoof](https://github.com/imaadhusoof))

The full commit history is kept, so you can see who worked on what.

## What we changed

In the starting code, each Tetris piece (I, J, L, O, S, T, Z) was its own class of almost 300 lines, with the same movement, rotation and collision logic copied into all seven. The 470-line `Tetris` class also handled creating pieces, choosing the next one, spawning it and recording statistics.

We restructured it around a few clear responsibilities.

- **`TetroPiece` abstract class:** every piece now extends one base class that holds all the shared behaviour, such as moving, rotating, dropping, collision checks and auto-play moves. Each piece class went from almost 300 lines to about 35, and `Tetris` went from 470 lines to 260.
- **`BlockPieces` enum:** we moved each piece's rotation coordinates into the existing enum, so its name, sprite index and shape all live in one place instead of being spread across the piece classes.
- **`PieceFactory` (Factory Method):** a single place that maps a piece type to its class. New pieces can be added without touching the rest of the game (Protected Variations).
- **`PieceManager` (Pure Fabrication):** takes over from `Tetris` in deciding which piece comes next, its spawn location and fall speed, and the preview of the next piece.
- **`StatisticsRecorder` (Pure Fabrication):** handles counting the pieces used each round and writing them to the statistics file, keeping file I/O out of the game logic.
- **`BlockAction` enum:** auto-play moves (left, right, turn) are parsed once into a typed enum instead of being compared as raw strings throughout the piece logic.
- **`TetrisConstants`:** replaces the magic numbers scattered across the project, such as the grid size, spawn position and timing values.

## New features

Both features can be switched on or off in the game's properties files.

- **Feature 1, madness pieces:** three new pieces called Cross (`X`), Plus (`+`) and Slash (`/`). They're made of 3 to 5 blocks and can't be rotated. Because of the refactor, adding them only needed a new class each and an entry in the enum and the factory.
- **Feature 2, random spawns and speeds:** pieces spawn at a random position in the top half of the board, avoiding spots that are already blocked, and fall at a random speed from 1 to 3.

The game also records how many of each piece was used every round and writes it to `Statistics.txt`.

## Design documents

[`design_class_diagram.pdf`](design_class_diagram.pdf) contains two diagrams. The first is the design class diagram of the refactored system, showing the `TetroPiece` hierarchy, the factory, the managers and how they relate. The second is the domain model, covering games, rounds, the board, pieces (simple vs. madness), blocks and statistics.

## My contributions

I worked across the project with the team, on the design as well as the implementation. I produced the design class diagram and the domain model in `design_class_diagram.pdf`.

## Running it

You'll need Java 21. Gradle downloads itself through the wrapper.

```
./gradlew run
```

On Windows, use `gradlew.bat run`.

You control the game with the arrow keys: left and right move the piece, up rotates it and down drops it.

The game reads its settings from `app/src/main/resources/properties/game1.properties` by default. In that file you can turn features 1 and 2 on (`active`) or off (`inactive`), and switch on auto-play with a fixed sequence of pieces, moves, speeds and spawn locations.

The JUnit tests in `app/src/test` replay these fixed sequences and check the game log. Run them with:

```
./gradlew test
```
