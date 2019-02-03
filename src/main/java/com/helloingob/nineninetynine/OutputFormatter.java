package com.helloingob.nineninetynine;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.helloingob.nineninetynine.data.ClassifiedAdvertisementTO;
import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.I;
import com.hp.gagawa.java.elements.Table;
import com.hp.gagawa.java.elements.Td;
import com.hp.gagawa.java.elements.Th;
import com.hp.gagawa.java.elements.Thead;
import com.hp.gagawa.java.elements.Tr;

public class OutputFormatter {

    public static Table createTableLayout() {
        Table table = new Table();

        table.setCellspacing("1");
        table.setCSSClass("tablesorter");

        table.setStyle("width:50%");

        Thead thead = new Thead();
        table.appendChild(thead);

        Tr tr = new Tr();
        thead.appendChild(tr);
        Th th = new Th();
        th.appendText("date");
        th.setWidth("100px");
        tr.appendChild(th);
        th = new Th();
        th.appendText("console");
        th.setWidth("60px");
        tr.appendChild(th);
        th = new Th();
        th.appendText("title");
        tr.appendChild(th);
        th = new Th();
        th.appendText("price (&euro;)");
        th.setWidth("60px");
        tr.appendChild(th);
        th = new Th();
        th.appendText("distance (km)");
        th.setWidth("90px");
        tr.appendChild(th);
        th = new Th();
        th.appendText("vb?");
        th.setWidth("35px");
        tr.appendChild(th);

        return table;
    }

    public static Tr createRowLayout(ClassifiedAdvertisementTO classifiedAdvertisementTO) {
        Tr tr = new Tr();

        Td td = new Td();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Settings.General.DEFAULT_TIME_FORMAT, Locale.ENGLISH);
        if (classifiedAdvertisementTO.getDate() != null) {
            td.appendText(simpleDateFormat.format(classifiedAdvertisementTO.getDate()));
        } else {
            td.appendText("-");
        }
        td.setAlign("center");
        tr.appendChild(td);
        td = new Td();
        td.appendText(classifiedAdvertisementTO.getConsole());
        td.setAlign("center");
        tr.appendChild(td);
        td = new Td();
        A a = new A();
        td.appendChild(a);
        a.setHref(classifiedAdvertisementTO.getLink());
        a.appendText(classifiedAdvertisementTO.getTitle());
        tr.appendChild(td);
        td = new Td();
        if (classifiedAdvertisementTO.getPrice() != null) {
            td.appendText(classifiedAdvertisementTO.getPrice().toString());
        } else {
            td.appendText("-");
        }
        td.setAlign("center");
        tr.appendChild(td);
        td = new Td();
        if (classifiedAdvertisementTO.getDistance() != null) {
            a = new A();
            td.appendChild(a);
            a.setHref("https://www.google.de/maps/place/" + classifiedAdvertisementTO.getPlz());
            a.setTarget("_blank");
            a.appendText(classifiedAdvertisementTO.getDistance().toString());
        } else {
            td.appendText("-");
        }
        td.setAlign("center");
        tr.appendChild(td);
        td = new Td();
        if (classifiedAdvertisementTO.isVb()) {
            td.appendChild(createSymbol(Settings.Output.YES_SYMBOL, null));
        } else {
            td.appendChild(createSymbol(Settings.Output.NO_SYMBOL, Settings.Output.INACTIVE_COLOR));
        }
        td.setAlign("center");
        tr.appendChild(td);

        return tr;
    }

    private static I createSymbol(String cssClass, String color) {
        I i = new I();
        if (color != null) {
            i.setStyle(color);
        }
        i.setCSSClass(cssClass);
        return i;
    }

    public static String createCopyright() {
        return "<div style=\"font-family: arial;font-size: 8pt;\">- <a href=\"mailto:code@inba.us\">1ngo 2015</a> -</div>";
    }

}
