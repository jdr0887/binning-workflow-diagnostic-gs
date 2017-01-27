package org.renci.binning.diagnostic.gs.commands;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.renci.binning.dao.BinningDAOBeanService;
import org.renci.binning.dao.BinningDAOException;
import org.renci.binning.dao.clinbin.model.DiagnosticBinningJob;
import org.renci.binning.diagnostic.gs.commons.LoadVCFCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Command(scope = "diagnostic-gs", name = "load-vcf", description = "Load VCF")
@Service
public class LoadVCFAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(LoadVCFAction.class);

    @Reference
    private BinningDAOBeanService binningDAOBeanService;

    @Option(name = "--binningJobId", description = "DiagnosticBinningJob Identifier", required = true, multiValued = false)
    private Integer binningJobId;

    public LoadVCFAction() {
        super();
    }

    @Override
    public Object execute() throws Exception {
        logger.debug("ENTERING execute()");

        DiagnosticBinningJob binningJob = binningDAOBeanService.getDiagnosticBinningJobDAO().findById(binningJobId);
        logger.info(binningJob.toString());

        try {
            binningJob.setStatus(binningDAOBeanService.getDiagnosticStatusTypeDAO().findById("VCF loading"));
            binningDAOBeanService.getDiagnosticBinningJobDAO().save(binningJob);

            ExecutorService es = Executors.newSingleThreadExecutor();
            es.submit(new LoadVCFCallable(binningDAOBeanService, binningJob));
            es.shutdown();
            es.awaitTermination(1L, TimeUnit.DAYS);

            binningJob.setStatus(binningDAOBeanService.getDiagnosticStatusTypeDAO().findById("VCF loaded"));
            binningDAOBeanService.getDiagnosticBinningJobDAO().save(binningJob);

        } catch (Exception e) {
            try {
                binningJob.setStop(new Date());
                binningJob.setFailureMessage(e.getMessage());
                binningJob.setStatus(binningDAOBeanService.getDiagnosticStatusTypeDAO().findById("Failed"));
                binningDAOBeanService.getDiagnosticBinningJobDAO().save(binningJob);
            } catch (BinningDAOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

}
