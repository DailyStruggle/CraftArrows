package io.github.dailystruggle.craftarrows.Objects;

import java.util.Random;

public class ArrowDropData {
    private final Random random = new Random();
    public boolean DropFixedAmount;
    public int Amount;
    public int Min;
    public int Max;

    public int getValue() {
        if (this.DropFixedAmount)
            return this.Amount;
        return this.random.nextInt(this.Max - this.Min) + this.Min;
    }
}
