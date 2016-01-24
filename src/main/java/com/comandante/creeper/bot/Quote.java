package com.comandante.creeper.bot;

public class Quote {

    private String triggerWord;
    private String quote;
    private long timestamp;

    public static QuoteBuilder builder = new QuoteBuilder();

    public Quote(String triggerWord, String quote, long timestamp) {
        this.triggerWord = triggerWord;
        this.quote = quote;
        this.timestamp = timestamp;
    }

    public static class QuoteBuilder {
        private String triggerWord;
        private String quote;
        private long timestamp;

        public QuoteBuilder triggerWord(String triggerWord) {
            this.triggerWord = triggerWord;
            return this;
        }

        public QuoteBuilder quote(String quote) {
            this.quote = quote;
            return this;
        }

        public QuoteBuilder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Quote build() {
            return new Quote(triggerWord, quote, timestamp);
        }
    }
}

