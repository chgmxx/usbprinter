package com.rockspoon.printertest.usbprinter;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lucas on 08/03/16 based on
 * http://stackoverflow.com/questions/19965209/use-the-device-filter-xml-resource-file-to-filter-usb-enumeration-results
 * Lekensteyn answer.
 */
public class UsbDeviceFilter {
  private final List<DeviceFilter> hostDeviceFilters;

  public UsbDeviceFilter(XmlPullParser parser) throws XmlPullParserException, IOException {
    hostDeviceFilters = new ArrayList<DeviceFilter>();

    int eventType = parser.getEventType();

    while (eventType != XmlPullParser.END_DOCUMENT) {
      String tagName = parser.getName();
      if ("usb-device".equals(tagName) && parser.getEventType() == XmlPullParser.START_TAG) {
        hostDeviceFilters.add(DeviceFilter.read(parser));
      }
      eventType = parser.next();
    }
  }

  public static List<UsbDevice> getMatchingHostDevices(final Context ctx, int resourceId) throws XmlPullParserException, IOException {
    final UsbManager usbManager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
    final XmlResourceParser parser = ctx.getResources().getXml(resourceId);
    final List<UsbDevice> matchedDevices = new ArrayList<UsbDevice>();
    UsbDeviceFilter devFilter;

    try {
      devFilter = new UsbDeviceFilter(parser);
    } finally {
      parser.close();
    }

    HashMap<String, UsbDevice> devices = usbManager.getDeviceList();

    for (final UsbDevice device : usbManager.getDeviceList().values()) {
      if (devFilter.matchesHostDevice(device)) {
        matchedDevices.add(device);
      }
    }
    return matchedDevices;
  }

  public boolean matchesHostDevice(final UsbDevice device) {
    for (final DeviceFilter filter : hostDeviceFilters) {
      if (filter.matches(device)) {
        return true;
      }
    }
    return false;
  }

  public static class DeviceFilter {

    public final int vendorId;
    public final int productId;
    public final int deviceClass;
    public final int deviceSubClass;
    public final int protocol;

    private DeviceFilter(int vendorId, int productId, int deviceClass, int deviceSubClass, int protocol) {
      this.vendorId = vendorId;
      this.productId = productId;
      this.deviceClass = deviceClass;
      this.deviceSubClass = deviceSubClass;
      this.protocol = protocol;
    }

    private static DeviceFilter read(final XmlPullParser parser) {
      int vendorId = -1;
      int productId = -1;
      int deviceClass = -1;
      int deviceSubclass = -1;
      int deviceProtocol = -1;

      int count = parser.getAttributeCount();
      for (int i = 0; i < count; i++) {
        String name = parser.getAttributeName(i);
        // All attribute values are ints
        int value = Integer.parseInt(parser.getAttributeValue(i));
        switch (name) {
          case "vendor-id": vendorId = value; break;
          case "product-id": productId = value; break;
          case "class": deviceClass = value; break;
          case "subclass": deviceSubclass = value; break;
          case "protocol": deviceProtocol = value; break;
        }
      }

      return new DeviceFilter(vendorId, productId, deviceClass, deviceSubclass, deviceProtocol);
    }

    private boolean matches(int clasz, int subclass, int protocol) {
      return ((deviceClass == -1 || clasz == deviceClass) && (deviceSubClass == -1 || subclass == deviceSubClass) && (this.protocol == -1 || protocol == this.protocol));
    }

    public boolean matches(final UsbDevice device) {
      if ( (vendorId != -1 && device.getVendorId() != vendorId) || (productId != -1 && device.getProductId() != productId)) {
        return false;
      }

      if (matches(device.getDeviceClass(), device.getDeviceSubclass(), device.getDeviceProtocol())) {
        return true;
      }

      int count = device.getInterfaceCount();
      for (int i = 0; i < count; i++) {
        final UsbInterface iface = device.getInterface(i);
        if (matches(iface.getInterfaceClass(), iface.getInterfaceSubclass(), iface.getInterfaceProtocol())) {
          return true;
        }
      }

      return false;
    }
  }
}
