package com.slapin.napt;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.main.Arguments;
import com.sun.tools.javac.util.Context;

public class NaptPlugin implements Plugin {

    @Override
    public String getName() {
        return "Napt";
    }

    @Override
    public void init(JavacTask task, String... args) {
        TaskListener listener = new AnnotationProcessingListener(task) {
            @Override
            void beforeAnnotationProcessing(Context context) {
                Arguments.instance(context).getClassNames().addAll(ClassesProvider.getClassNames(context));
            }
        };
        task.addTaskListener(listener);
    }
}