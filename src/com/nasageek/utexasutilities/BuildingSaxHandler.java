
package com.nasageek.utexasutilities;

import com.nasageek.utexasutilities.model.BuildingPlacemark;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BuildingSaxHandler extends PlacemarkSaxHandler<BuildingPlacemark> {

    /**
     * Called on opening tags like: <tag> Can provide attribute(s), when xml was
     * like: <tag attribute="attributeValue">
     */
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
            throws SAXException {
        switch (localName) {
            case "Placemark":
                currentPlacemark = new BuildingPlacemark();
                if (atts.getLength() > 0) {
                    currentPlacemark.setTitle(atts.getValue(0));
                }
                break;
            case "name":
                this.in_nametag = true;
                break;
            case "coordinates":
                buffer = new StringBuffer();
                this.in_coordinatestag = true;
                break;
        }
    }

    /**
     * Called on closing tags like: </tag>
     */
    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        switch (localName) {
            case "Placemark":
                placemarks.push(currentPlacemark);
                break;
            case "name":
                this.in_nametag = false;
                break;
            case "coordinates":
                currentPlacemark.setCoordinates(buffer.toString().trim());
                this.in_coordinatestag = false;
                break;
        }
    }

    /**
     * Called on the following structure: <tag>characters</tag>
     */
    @Override
    public void characters(char ch[], int start, int length) {
        if (this.in_nametag) {
            if (currentPlacemark == null) {
                currentPlacemark = new BuildingPlacemark();
            }
            String description = (currentPlacemark.getDescription() == null) ? ""
                    : currentPlacemark.getDescription();
            currentPlacemark.setDescription(description.concat(new String(ch, start, length)));
        } else if (this.in_coordinatestag) {
            if (currentPlacemark == null) {
                currentPlacemark = new BuildingPlacemark();
            }
            buffer.append(ch, start, length);
        }
    }
}
