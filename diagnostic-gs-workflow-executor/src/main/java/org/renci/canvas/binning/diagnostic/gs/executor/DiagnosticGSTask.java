package org.renci.canvas.binning.diagnostic.gs.executor;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiagnosticGSTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticGSTask.class);

    private Integer binningJobId;

    public DiagnosticGSTask() {
        super();
    }

    public DiagnosticGSTask(Integer binningJobId) {
        super();
        this.binningJobId = binningJobId;
    }

    @Override
    public void run() {
        logger.debug("ENTERING run()");

        try {
            BundleContext bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();

            ServiceReference<RepositoryService> repositoryServiceReference = bundleContext.getServiceReference(RepositoryService.class);
            RepositoryService repositoryService = bundleContext.getService(repositoryServiceReference);

            ServiceReference<RuntimeService> runtimeServiceReference = bundleContext.getServiceReference(RuntimeService.class);
            RuntimeService runtimeService = bundleContext.getService(runtimeServiceReference);

            repositoryService.createDeployment().addClasspathResource("org/renci/binning/diagnostic/gs/executor/gs.bpmn20.xml").deploy();

            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("binningJobId", binningJobId);

            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("gs_diagnostic_binning", variables);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Integer getBinningJobId() {
        return binningJobId;
    }

    public void setBinningJobId(Integer binningJobId) {
        this.binningJobId = binningJobId;
    }

}
