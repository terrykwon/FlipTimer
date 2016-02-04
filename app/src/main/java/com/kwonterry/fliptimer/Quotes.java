package com.kwonterry.fliptimer;

import java.util.Random;

/**
 * Created by Terry Kwon on 2/4/2016.
 */
public class Quotes {

    private static Random random = new Random();

    private static String[] quotes = {
        "Impossible is nothing.\n- Adidas ",
            "Just do it.\n- Nike",
            "Go for it now. The future is promised to no one. \n- Wayne Dyer",
            "The secret of getting ahead is getting started. \n- Mark Twain",
            "You are never too old to set another goal or to dream a new dream.\n- C. S.Lewis",
            "Our greatest weakness lies in giving up. The most certain way to succeed is always to try just one more time.\n- Thomas Edison",
            "Keep your eyes on the stars, and your feet on the ground.\n- Theodore Roosevelt",
            "I know where I'm going and I know the truth, and I don't have to be what you want me to be. I'm free to be what I want.\n- Muhammad Ali",
            "Well done is better than well said.\n- Benjamin Franklin",
            "If you're going through hell, keep going.\n- Winston Churchill"
    };

    public static String getRandomQuote() {
        String quote =  quotes[random.nextInt(quotes.length)];
        return quote;
    }
}
