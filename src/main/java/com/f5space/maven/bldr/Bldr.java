package com.f5space.maven.bldr;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.ArrayIterator;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;


import java.beans.Beans;
import java.beans.PropertyDescriptor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Goal which runs the bldr bldr
 *
 * @goal bldr
 * 
 * @phase process-classes
 */
public class Bldr extends AbstractMojo
{
    /**
     * Location of the file.
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File inputDirectory;
    
    /**
     * Location of the output file.
     * @parameter expression="${project.build.sourceDirectory}/../java-gen"
     * @required
     */
    private File outputDirectory;
    
    /**
     * Packages to make builders with
     * @parameter expression="${bldr.packageToMakeBldr}"
     * @required
     */
    private String packageToMakeBldr;

    public void execute() throws MojoExecutionException
    {
        Set<Class<?>> classes = getClasses(inputDirectory, packageToMakeBldr);
        
        PropertyUtilsBean pub = new PropertyUtilsBean();
        StringTemplateGroup templates = new StringTemplateGroup("group1");
        
        for(Class<?> clazz : classes)
        {
        	BldrProperties bldr = new BldrProperties();
        	
        	bldr.pkg = clazz.getPackage().getName() + "._bldr";
        	
        	// make directory.
        	String bldrPackageFolder = outputDirectory.getAbsolutePath() + File.separator + bldr.pkg.replaceAll("\\.", File.separator);
        	
        	new File(bldrPackageFolder).mkdirs();
        	
        	bldr.className = clazz.getSimpleName() + "Bldr";
        	bldr.type = clazz.getCanonicalName();
        	
        	for(PropertyDescriptor desc : pub.getPropertyDescriptors(clazz))
        	{
        		System.out.println(desc);
        		if(desc.getName().equals("class")){ continue; }
        		
        		System.out.println(desc.getName());
        		
        		BldProp prop = new BldProp();
        		System.out.println(desc.getWriteMethod());
        		prop.setMethodName = desc.getWriteMethod().getName();
        		
        		StringBuilder argSpec = new StringBuilder();
        		StringBuilder argList = new StringBuilder();
        		ArrayIterator ai = new ArrayIterator(desc.getWriteMethod().getParameterTypes());
        		int i=0;
        		while( ai.hasNext() ) {
        			argSpec.append(((Class<?>)ai.next()).getCanonicalName());
        			argSpec.append(" ").append("arg").append(i);
        			argList.append("arg").append(i);
        			if(ai.hasNext()) {
        				argSpec.append(", ");
        				argList.append(", ");
        			}
        			i++;
        		}
        		
        		prop.propArgsSpec = argSpec.toString();
        		prop.propArgsList = argList.toString();
        		prop.propNameU = desc.getName().substring(0,1).toUpperCase()+desc.getName().substring(1);
        		bldr.bldrs.add(prop);
        	}
        	
        	
        	StringTemplate tpl = templates.getInstanceOf("com/f5space/maven/bldr/class");
        	
        	tpl.setAttribute("bldr", bldr);
        	
        	String classText = tpl.toString();
        	File classFile = new File(bldrPackageFolder + File.separator + bldr.className + ".java");
        	
        	
        	
        	try {
        		System.out.println(classFile.getAbsolutePath());
        		classFile.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(classFile));
				
				bw.write(classText);
				bw.flush();
				bw.close();
			} 
        	catch (IOException e) {
        		e.printStackTrace();
			}
        	
        }
    }
    
    public Set<Class<? extends Object>> getClasses(File inputDirectory, String packageToMakeBldr) throws MojoExecutionException
    {
    	try {
            
			//getLog().error("inputDirectory -> " + inputDirectory.getCanonicalPath());
			//getLog().error("package -> " + packageToMakeBldr);
        
			String urlString =  inputDirectory.getAbsolutePath();
			
			if(! urlString.endsWith("/") ) {
				urlString = urlString + "/";
			}
			
			URLClassLoader urlc = new URLClassLoader( new URL[]{new URL("file://" + urlString)});
			
			FilterBuilder fb = new FilterBuilder().include( packageToMakeBldr.replace(".", "\\.") + "\\.[a-zA-Z0-9_]+\\.class"); //FilterBuilder.prefix("com.f5space.mainstack.entity"));
			
			ConfigurationBuilder cb = new ConfigurationBuilder()
										.addClassLoader(urlc)
										.setUrls(ClasspathHelper.forClassLoader(urlc))
										.filterInputsBy(fb)
										.setScanners(new SubTypesScanner(false));
			
			Reflections refl = 
					new Reflections(cb);
			
			
			Set<Class<? extends Object>> classes = refl.getSubTypesOf(Object.class);
			classes.remove(null); // important
			
			return classes;
			
        }
        catch(Exception e) {
        	e.printStackTrace();
        	throw new MojoExecutionException("Error scanning classes", e);
        	
        }
    }
    
    private static class BldrProperties
    {
    	public String pkg;
    	public String type;
    	public String className;
    	public List<BldProp> bldrs = new ArrayList<BldProp>();
    }
    
    private static class BldProp
    {
    	public String propNameU;
    	public String setMethodName;
    	public String propArgsSpec;
    	public String propArgsList;
    }
}
