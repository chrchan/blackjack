package cchan.blackjack.model;

public enum Card {
    CA("A", 1), //
    C2("2", 2), //
    C3("3", 3), //
    C4("4", 4), //
    C5("5", 5), //
    C6("6", 6), //
    C7("7", 7), //
    C8("8", 8), //
    C9("9", 9), //
    CT("T", 10), //
    CJ("J", 10), //
    CQ("Q", 10), //
    CK("K", 10) //
    ;

    private String name;

    private int value;

    private Card(String name, int value) {
    	this.name = name;
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public String toString() {
    	return name;
    }

}
