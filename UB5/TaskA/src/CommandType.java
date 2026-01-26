public enum CommandType {
    QUIT("quit"),
    ADD("add"),
    ADD_LIST("add-list"),
    TAG("tag"),
    ASSIGN("assign"),
    TOGGLE("toggle"),
    CHANGE_DATE("change-date"),
    CHANGE_PRIORITY("change-priority"),
    DELETE("delete"),
    RESTORE("restore"),
    SHOW("show"),
    TODO("todo"),
    LIST("list"),
    TAGGED_WITH("tagged-with"),
    FIND("find"),
    UPCOMING("upcoming"),
    BEFORE("before"),
    BETWEEN("between"),
    DUPLICATES("duplicates");

    private final String commandName;

    CommandType(String commandName) {
        this.commandName = commandName;
    }

    public String getName() {
        return commandName;
    }
}
