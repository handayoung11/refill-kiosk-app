package kr.co.nicevan.nvcat.dto;

import static kr.co.nicevan.nvcat.PrinterControl.PrinterType.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import kr.co.nicevan.nvcat.PrinterControl.PrinterType;

public class PrinterDTO {

    private List<String> output;
    private String img;
    private PrintConfig config;
    private PrinterType type;

    private PrinterDTO(@NonNull List<String> strings, @Nullable String img, PrintConfig config, PrinterType type) {
        this.output = strings;
        this.img = img;
        this.config = config;
        this.type = type;
    }
    public PrinterDTO(@NonNull String string, @Nullable String img, PrinterType type) {
        List<String> init = new ArrayList<>();
        init.add(string);
        this.output = init;
        this.img = img;
        this.config = PrinterDTO.PrintConfig.of(null);
        this.type = type;
    }

    public static PrinterDTO of(@NonNull List<String> strings, @Nullable String img, PrinterType type){
        PrintConfig attribute = PrintConfig.of(type);
        return new PrinterDTO(strings, img, attribute, type);
    }

    public List<String> getOutput() {return output;}
    public String getImg() {return img;}
    public PrintConfig getConfig() {return config;}
    public PrinterType getType() {return type;}

    public static class PrintConfig {
        private int alignment;
        private int attribute;
        private int spinnerSize;

        private PrintConfig(int alignment, int attribute, int spinnerSize) {
            this.alignment = alignment;
            this.attribute = attribute;
            this.spinnerSize = spinnerSize;
        }

        public static PrintConfig of(int alignment, int attribute, int spinnerSize){
            return new PrintConfig(alignment, attribute, spinnerSize);
        }

        private static PrintConfig of(@Nullable PrinterType type){
            PrintConfig attribute;
            if(type.equals(RECEIPT)) attribute = new PrintConfig(1, 1,1);
            else if(type.equals(LABEL)) attribute = new PrintConfig(1, 2, 1);
            else attribute = new PrintConfig(1, 1,0);
            return attribute;
        }

        public int getAlignment() {
            return alignment;
        }

        public int getAttribute() {
            return attribute;
        }

        public int getSpinnerSize() {
            return spinnerSize;
        }
    }


}
