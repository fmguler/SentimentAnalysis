/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package startupistanbul.kodinghackathon.service;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Generics;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import static startupistanbul.kodinghackathon.service.SentimentService.Output.PENNTREES;
import static startupistanbul.kodinghackathon.service.SentimentService.Output.ROOT;

/**
 *
 * @author Fatih
 */
public class SentimentService {
    private StanfordCoreNLP tokenizer;
    private StanfordCoreNLP pipeline;

    static enum Output {
        PENNTREES, VECTORS, ROOT, PROBABILITIES
    }

    public void init() {
        Properties pipelineProps = new Properties();
        Properties tokenizerProps = null;
        pipelineProps.setProperty("annotators", "parse, sentiment");
        pipelineProps.setProperty("enforceRequirements", "false");
        tokenizerProps = new Properties();
        tokenizerProps.setProperty("annotators", "tokenize, ssplit");

        tokenizer = (tokenizerProps == null) ? null : new StanfordCoreNLP(tokenizerProps);
        pipeline = new StanfordCoreNLP(pipelineProps);

    }

    /**
     * Analyze a tweet and return its sentiment
     *
     * @param tweet
     * @return 1 if positive -1 if negative 0 if neutral
     */
    public Integer analyzeTweet(String tweet) {
        Annotation annotation = new Annotation(tweet);
        tokenizer.annotate(annotation);

        List<Annotation> annotations = Generics.newArrayList();
        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            Annotation nextAnnotation = new Annotation(sentence.get(CoreAnnotations.TextAnnotation.class));
            nextAnnotation.set(CoreAnnotations.SentencesAnnotation.class, Collections.singletonList(sentence));
            annotations.add(nextAnnotation);
        }

        //the total sentiment calculated by counting individual sentences in the tweet
        int totalSentiment = 0;

        //annotate all sentences one by one
        for (Annotation ann : annotations) {
            pipeline.annotate(ann);

            for (CoreMap sentence : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
                //System.out.println(sentence);
                //System.out.println(sentence.get(SentimentCoreAnnotations.ClassName.class));

                String sentenceSentiment = sentence.get(SentimentCoreAnnotations.ClassName.class);
                if (sentenceSentiment.equals("Negative") || sentenceSentiment.equals("Very negative")) {
                    totalSentiment--;
                } else if (sentenceSentiment.equals("Positive") || sentenceSentiment.equals("Very positive")) {
                    totalSentiment++;
                }
            }
        }

        return totalSentiment > 0 ? 1 : totalSentiment < 0 ? -1 : 0;
    }

    /**
     * Outputs a tree using the output style requested
     */
    static void outputTree(PrintStream out, CoreMap sentence, List<Output> outputFormats) {
        Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
        for (Output output : outputFormats) {
            switch (output) {
                case ROOT: {
                    out.println("  " + sentence.get(SentimentCoreAnnotations.ClassName.class));
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown output format " + output);
            }
        }
    }

    static final String DEFAULT_TLPP_CLASS = "edu.stanford.nlp.parser.lexparser.EnglishTreebankParserParams";

    public static void main(String[] args) {
        SentimentService sentimentService = new SentimentService();
        sentimentService.init();

        System.out.println(sentimentService.analyzeTweet("We were about to fail.\nBut then we found Stanford Sentiment Analyzer.\nNow we're back in track again, yay!"));
        System.out.println(sentimentService.analyzeTweet("This concert absolutely rocks!!!"));
    }

}
