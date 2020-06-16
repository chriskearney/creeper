package com.comandante.creeper.core_game;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RecentChangesManager {

    private static LoadingCache<String, String> recentChanges = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, String>() {
                        public String load(String key) { // no checked exception
                            return _getRecentChanges();
                        }
                    });

    private static String _getRecentChanges() {
        StringBuilder sb = new StringBuilder();
        try {
            GitHub github = GitHub.connectAnonymously();
            GHRepository repo = github.getRepository("chriskearney/creeper");
            PagedIterable<GHCommit> list = repo.queryCommits().list();
            int i = 1;
            for (GHCommit ghCommit : list.asList()) {
                if (i < 10) {
                    sb.append("Change #" + i + " | " + ghCommit.getCommitShortInfo().getMessage() + "\r\n");
                } else {
                    continue;
                }
                i++;
            }
        } catch (IOException e) {
            SentryManager.logSentry(RecentChangesManager.class, e, "Recent changes problem!");
        }
        return sb.toString();
    }

    public static String getRecentChanges() throws ExecutionException {
        return recentChanges.get("recent_changes");
    }

}
