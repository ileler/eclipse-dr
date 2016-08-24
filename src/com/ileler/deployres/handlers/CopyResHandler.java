package com.ileler.deployres.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class CopyResHandler extends AbstractHandler {
    
    private static final String CFG_NAME = "dr.cfg";

	public CopyResHandler() {}

	public Object execute(ExecutionEvent event) throws ExecutionException {  
	    IWorkbenchWindow window = HandlerUtil  
	            .getActiveWorkbenchWindowChecked(event);  
	    ISelection sel = window.getSelectionService().getSelection();  
	    if (sel instanceof IStructuredSelection) {  
	        Object obj = ((IStructuredSelection) sel).getFirstElement();  
	        String path = null;  

	        if (obj instanceof IResource) {  
	            path = ((IResource) obj).getLocation().toOSString();  
	        }  
	        if (path != null) {  
	            try {  
	                File selected = new File(path);
	                File cfgFile = getCfgFile(selected);
	                if (cfgFile == null) {
	                    MessageDialog.openInformation(Display.getDefault().getActiveShell(), "DeployRes", "not found ["+CFG_NAME+"].");
	                    return null;
	                }
	                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(cfgFile)));
                    String targetBasePath = br.readLine();
                    br.close();
                    if (targetBasePath == null || "".equals(targetBasePath.trim()))    return null;
                    File desc = new File(targetBasePath + File.separator + selected.getPath().replace(cfgFile.getParent(), ""));
                    File src = selected.equals(cfgFile) ? selected.getParentFile() : selected;
                    File des = selected.equals(cfgFile) ? desc.getParentFile() : desc;
                    copy(src, des);
                    String message = "Copy\n[" + src.getPath() + "]\nto\n[" + des.getPath() + "]\n\nDone.";
                    MessageDialog.openInformation(Display.getDefault().getActiveShell(), "DeployRes", message);
	            } catch (Exception e) {  
	                MessageDialog.openError(Display.getDefault().getActiveShell(), "DeployRes", e.getMessage());
	            }  
	        }  
	  
	    }  
	    return null;  
	}  
	
	private File getCfgFile(File selected) {
	    if (selected == null || !selected.exists())    return null;
	    if (selected.isDirectory()) {
	        File cfgFile = new File(selected.getPath() + File.separator + CFG_NAME);
	        if (cfgFile.exists()) {
	            return cfgFile;
	        } else {
	            return getCfgFile(selected.getParentFile());
	        }
	    } else {
	        return getCfgFile(selected.getParentFile());
	    }
	}
	
	private void copy(File src, File des) throws IOException {
        if (src == null || des == null)     return;
        if (src.isDirectory()) {
            File[] files = src.listFiles();
            des.mkdirs();
            for (File file : files) {
                copy(file, new File(des.getAbsolutePath() + File.separator + file.getName()));
            }
        } else {
            if (src.getName().equals(CFG_NAME)) return;
            des.getParentFile().mkdirs();
            try (InputStream is = new FileInputStream(src);
                    OutputStream os = new FileOutputStream(des);) {
                byte[] bs = new byte[2048];
                int len = -1;
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }
}
