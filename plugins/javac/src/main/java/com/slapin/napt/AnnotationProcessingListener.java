package com.slapin.napt;

import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.api.BasicJavacTask;

abstract class AnnotationProcessingListener implements TaskListener {

    private final Context context;

    public AnnotationProcessingListener(JavacTask javacTask) {
        context = ((BasicJavacTask) javacTask).getContext();
    }

    abstract void beforeAnnotationProcessing(Context context);

    @Override
    public final void started(TaskEvent e) {
        if (e.getKind() == TaskEvent.Kind.ANNOTATION_PROCESSING) beforeAnnotationProcessing(context);
    }

    @Override
    public final void finished(TaskEvent e) {
    }
}