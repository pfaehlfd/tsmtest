/*******************************************************************************
 * Copyright (c) 2012-2013 Albert Flaig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Albert Flaig - initial version
 *
 *******************************************************************************/
package net.sourceforge.tsmtest.datamodel.providers;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author Albert Flaig
 *
 */
public class TSMFileTransfer extends ByteArrayTransfer {

    private static final String TSMResourceNAME = "net.sourceforge.tsmtest.resource";
    private static final int TSMResourceID = registerType(TSMResourceNAME);
    private static TSMFileTransfer instance = new TSMFileTransfer();

    private TSMFileTransfer() {
    }

    public static TSMFileTransfer getInstance() {
	return instance;
    }

    @Override
    public void javaToNative(final Object object,
	    final TransferData transferData) {
	// if (object == null || !(object instanceof TSMResource[])) {
	// return;
	// }
	//
	// if (isSupportedType(transferData)) {
	// final TSMResource[] TSMResources = (TSMResource[]) object;
	// try {
	// // write data to a byte array and then ask super to convert to
	// // pMedium
	// final ByteArrayOutputStream out = new ByteArrayOutputStream();
	// final DataOutputStream writeOut = new DataOutputStream(out);
	// for (int i = 0; i < TSMResources.length; i++) {
	// final byte[] buffer = TSMResources[i].fileName.getBytes();
	// writeOut.writeInt(buffer.length);
	// writeOut.write(buffer);
	// writeOut.writeLong(TSMResources[i].fileLength);
	// writeOut.writeLong(TSMResources[i].lastModified);
	// }
	// final byte[] buffer = out.toByteArray();
	// writeOut.close();
	//
	// super.javaToNative(buffer, transferData);
	//
	// } catch (final IOException e) {
	// }
	// }
    }

    @Override
    public Object nativeToJava(final TransferData transferData) {
	//
	// if (isSupportedType(transferData)) {
	//
	// final byte[] buffer = (byte[]) super.nativeToJava(transferData);
	// if (buffer == null) {
	// return null;
	// }
	//
	// TSMResource[] myData = new TSMResource[0];
	// try {
	// final ByteArrayInputStream in = new ByteArrayInputStream(buffer);
	// final DataInputStream readIn = new DataInputStream(in);
	// while (readIn.available() > 20) {
	// final TSMResource datum = new TSMResource();
	// final int size = readIn.readInt();
	// final byte[] name = new byte[size];
	// readIn.read(name);
	// datum.fileName = new String(name);
	// datum.fileLength = readIn.readLong();
	// datum.lastModified = readIn.readLong();
	// final TSMResource[] newMyData = new TSMResource[myData.length + 1];
	// System.arraycopy(myData, 0, newMyData, 0, myData.length);
	// newMyData[myData.length] = datum;
	// myData = newMyData;
	// }
	// readIn.close();
	// } catch (final IOException ex) {
	// return null;
	// }
	// return myData;
	// }
	//
	return null;
    }

    @Override
    protected String[] getTypeNames() {
	return new String[] { TSMResourceNAME };
    }

    @Override
    protected int[] getTypeIds() {
	return new int[] { TSMResourceID };
    }

}
