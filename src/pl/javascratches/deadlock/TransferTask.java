package pl.javascratches.deadlock;

public record TransferTask(
        Account sender,
        Account receiver,
        int amount) {
}
