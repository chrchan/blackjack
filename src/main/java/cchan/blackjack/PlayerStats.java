package cchan.blackjack;

public class PlayerStats {
    
    private int blackjacks = 0;
    
    private int cash = 0;
    
    private int pushes = 0;
    
    private int losses = 0;
    
    private int wins = 0;
    
    public PlayerStats(int startingCash) {
        this.cash = startingCash;
    }

    public int getBlackjacks() {
        return this.blackjacks;
    }

    public void incrBlackjacks() {
        this.blackjacks++;
    }

    public int getCash() {
        return this.cash;
    }

    public int getLosses() {
        return this.losses;
    }

    public int getPushes() {
        return this.pushes;
    }

    public int getWins() {
        return this.wins;
    }

    public void incrCash(int amt) {
        this.cash += amt;
    }

    public void incrLosses() {
        this.losses++;
    }

    public void incrPushes() {
        this.pushes++;
    }

    public void incrWins() {
        this.wins++;
    }
    
}
