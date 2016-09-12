package com.ileler.deployres.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.team.ui.synchronize.ISynchronizeModelElement;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class CopyResHandler extends AbstractHandler {
    
    private static final String CFG_NAME = "dr.cfg";
    private Map<String, StringBuffer> message;
    private List<String> cfgPaths;

	public CopyResHandler() {}

	public Object execute(ExecutionEvent event) throws ExecutionException {  
	    IWorkbenchWindow window = HandlerUtil  
	            .getActiveWorkbenchWindowChecked(event);  
	    ISelection sel = window.getSelectionService().getSelection();  
	    if (sel instanceof IStructuredSelection) {  
	        message = new Hashtable<>();
            List<?> list = ((IStructuredSelection) sel).toList();
            for (Object _sel : list) {
                execdetail(_sel);
            }
            Iterator<String> iter = message.keySet().iterator();
            String msg = "";
            while (iter.hasNext()) {
                String _str = iter.next();
                msg += ("[" + _str + "]:\n" + message.get(_str).toString() + "\n");
            }
            MessageDialog.openInformation(Display.getDefault().getActiveShell(), "ArcUtil", !"".equals(msg) ? msg : "none.");
	    }  
	    return null;  
	}  
	
	private void execdetail(Object obj) {
	    String path = null;
	    IResource res = null;
	    if (obj instanceof IResource) {
	        res = ((IResource) obj);
        } else if (obj instanceof IJavaElement) {
            res = ((IJavaElement) obj).getResource();
        } else if (obj instanceof ISynchronizeModelElement) {
            res = ((ISynchronizeModelElement) obj).getResource();
        } else {
            return;
        } 
	    IPath iPath = res == null ? null : res.getLocation();
        if (iPath != null && (path = iPath.toOSString()) != null) {  
            String project = res.getProject().getName();
            StringBuffer _message = null;
            if (!message.containsKey(project)) {
                _message = new StringBuffer();
                message.put(project, _message);
            } else {
                _message = message.get(project);
            }
            String _path =  res.getProjectRelativePath().toOSString();
            try {  
                File selected = new File(path);
                File cfgFile = getCfgFile(selected);
                if (cfgFile == null) {
                    _message.append(_path + "   -not found ["+CFG_NAME+"]\n");
                    return;
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(cfgFile)));
                String targetBasePath = br.readLine();
                br.close();
                if (targetBasePath == null || "".equals(targetBasePath.trim())) {
                    _message.append(_path + "   -cfg invalid ["+CFG_NAME+"]\n");
                    return;
                }
                File desc = new File(targetBasePath + File.separator + selected.getPath().replace(cfgFile.getParent(), ""));
                File src = selected.equals(cfgFile) ? selected.getParentFile() : selected;
                File des = selected.equals(cfgFile) ? desc.getParentFile() : desc;
                copy(src, des);
                _message.append(_path + "   -done.\n");
            } catch (Exception e) {  
                MessageDialog.openError(Display.getDefault().getActiveShell(), "DeployRes", e.getMessage());
            }  
        }
	}
	
	private File getCfgFile(File selected) {
	    if (selected == null || !selected.exists())    return null;
	    if (cfgPaths != null) {
	        String _path = selected.getPath();
	        for (String cfgPath : cfgPaths) {
	            if (_path.startsWith(cfgPath)) return new File(cfgPath);
	        }
	    }
	    if (selected.isDirectory()) {
	        File cfgFile = new File(selected.getPath() + File.separator + CFG_NAME);
	        if (cfgFile.exists()) {
	            if (cfgPaths == null) {
	                cfgPaths = new ArrayList<>();
	            }
	            cfgPaths.add(cfgFile.getPath());
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
