package kr.co.nicevan.nvcat.dto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PrinterDTO {

    private List<String> output;
    private String img;

    public PrinterDTO(@NonNull List<String> strings, @Nullable String img) {
        this.output = strings;
        this.img = img;
    }
    public PrinterDTO(@NonNull String string, @Nullable String img) {
        List<String> init = new ArrayList<>();
        init.add(string);
        this.output = init;
        this.img = img;
    }
    public List<String> getOutput() {return output;}
    public String getImg() {return img;}
}
