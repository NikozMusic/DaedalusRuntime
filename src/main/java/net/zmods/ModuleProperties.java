package net.zmods;

public record ModuleProperties(
    DataProperties data,
    InfoProperties info
) {
    public record DataProperties(
            String id,
            String moduleVersion,
            boolean containsData,
            boolean containsCommands,
            short loadPriority
    ) { }

    public record InfoProperties(
            String name,
            String description,
            String author,
            String credits
    ) { }
}

