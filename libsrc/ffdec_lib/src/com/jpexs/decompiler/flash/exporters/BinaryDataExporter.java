/*
 *  Copyright (C) 2010-2023 JPEXS, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.jpexs.decompiler.flash.exporters;

import com.jpexs.decompiler.flash.AbortRetryIgnoreHandler;
import com.jpexs.decompiler.flash.EventListener;
import com.jpexs.decompiler.flash.ReadOnlyTagList;
import com.jpexs.decompiler.flash.RetryTask;
import com.jpexs.decompiler.flash.configuration.Configuration;
import com.jpexs.decompiler.flash.exporters.settings.BinaryDataExportSettings;
import com.jpexs.decompiler.flash.tags.DefineBinaryDataTag;
import com.jpexs.decompiler.flash.tags.Tag;
import com.jpexs.helpers.Helper;
import com.jpexs.helpers.Path;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author JPEXS
 */
public class BinaryDataExporter {

    public List<File> exportBinaryData(AbortRetryIgnoreHandler handler, String outdir, ReadOnlyTagList tags, BinaryDataExportSettings settings, EventListener evl) throws IOException, InterruptedException {
        List<File> ret = new ArrayList<>();
        if (Thread.currentThread().isInterrupted()) {
            return ret;
        }

        if (tags.isEmpty()) {
            return ret;
        }

        File foutdir = new File(outdir);
        Path.createDirectorySafe(foutdir);

        int count = 0;
        for (Tag t : tags) {
            if (t instanceof DefineBinaryDataTag) {
                count++;
            }
        }

        if (count == 0) {
            return ret;
        }

        int currentIndex = 1;
        for (final Tag t : tags) {
            if (t instanceof DefineBinaryDataTag) {
                DefineBinaryDataTag bdt = (DefineBinaryDataTag) t;
                if (evl != null) {
                    evl.handleExportingEvent("binarydata", currentIndex, count, t.getName());
                }

                String ext = bdt.innerSwf == null ? ".bin" : ".swf";
                final File file = new File(outdir + File.separator + Helper.makeFileName(bdt.getCharacterExportFileName() + ext));
                new RetryTask(() -> {
                    try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(file))) {
                        fos.write(bdt.binaryData.getRangeData());
                    }
                }, handler).run();
                
                Set<String> classNames = bdt.getClassNames();                    
                if (Configuration.as3ExportNamesUseClassNamesOnly.get() && !classNames.isEmpty()) {
                    for (String className : classNames) {
                        File classFile = new File(outdir + File.separator + Helper.makeFileName(className + ext));
                        new RetryTask(() -> {
                            Files.copy(file.toPath(), classFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        },handler).run();
                        ret.add(classFile);
                    }
                    file.delete();
                } else {                
                    ret.add(file);
                }

                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                if (evl != null) {
                    evl.handleExportedEvent("binarydata", currentIndex, count, t.getName());
                }

                currentIndex++;
            }
        }

        return ret;
    }
}
