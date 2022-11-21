package org.example;

import de.fraunhofer.iem.secucheck.analysis.implementation.SingleFlowTaintAnalysis.BoomerangSolver.Utility;
import de.fraunhofer.iem.secucheck.analysis.query.EntryPoint;
import soot.Scene;
import soot.SootClass;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.config.IInfoflowConfig;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.results.DataFlowResult;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.options.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FlowDroidDemo {
    protected static List<String> sources;
    protected static List<String> sinks;
    protected static String classname = "test.testFastjsonDev";
    protected static final String source1 = "<gov.nasa.jpf.jdart.SymbolicString: java.lang.String makeConcolicString(java.lang.String)>";
    protected static final String sink1 = "<com.alibaba.fastjson.parser.deserializer.IntegerDeserializer: java.lang.Object deserialze(com.alibaba.fastjson.parser.DefaultJSONParser,java.lang.reflect.Type,java.lang.Object)>";
//    protected static final String sink1 = "<com.alibaba.fastjson.parser.deserializer.ObjectDeserializer: java.lang.Object deserialze(com.alibaba.fastjson.parser.DefaultJSONParser,java.lang.reflect.Type,java.lang.Object)>";
//    protected static final String sink1 = "<com.alibaba.fastjson.parser.DefaultJSONParser: java.lang.Object parse()>";
//    protected static final String sink1 = "<com.alibaba.fastjson.parser.DefaultJSONParser: java.lang.Object parseObject(java.util.Map,java.lang.Object)>";

    protected static final String entry_point = "<"+classname+": void main(java.lang.String[])>";


    protected static String appPath, libPath;
    public void run() throws Exception {

//     libPath = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";
        libPath = "";

        appPath = "/home/dyd/project/jpf-concolic/jpf-concolic/build-idea";

        sources = new ArrayList<String>();
        sources.add(source1);

        sinks = new ArrayList<String>();
        sinks.add(sink1);

        List<String> epoints = new ArrayList<String>();
        epoints.add(entry_point);

        Infoflow infoFlow = getInfoFlow();
        EasyTaintWrapper easyTaintWrapper = new EasyTaintWrapper(new File("EasyTaintWrapperSource.txt"));


        infoFlow.setTaintWrapper(easyTaintWrapper);
        infoFlow.computeInfoflow(appPath, libPath, epoints, sources, sinks);

        if (infoFlow.isResultAvailable()) {
            InfoflowResults map = infoFlow.getResults();
            if (map.size() > 0) {
                Iterator iterator = map.getResultSet().iterator();
                while(iterator.hasNext()) {
                    DataFlowResult dataFlowResult = (DataFlowResult)iterator.next();
                    System.out.println(dataFlowResult.toString());
                }
            }
        }
    }

    private static Infoflow getInfoFlow() {
        Infoflow infoFlow = new Infoflow();
        infoFlow.setSootConfig(new IInfoflowConfig() {

            @Override
            public void setSootOptions(Options options, InfoflowConfiguration config) {
                // TODO: set included packages.
                // options.set_include(includeList);
                options.set_exclude(Utility.excludedPackages());
                options.set_output_format(Options.output_format_none);
            }
        });
        infoFlow.getConfig().setInspectSinks(false);
        infoFlow.getConfig().setInspectSources(false);
        infoFlow.getConfig().setLogSourcesAndSinks(true);
        return infoFlow;
    }

}
