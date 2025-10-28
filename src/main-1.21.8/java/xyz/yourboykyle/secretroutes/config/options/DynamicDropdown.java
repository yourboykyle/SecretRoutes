package xyz.yourboykyle.secretroutes.config.options;

import cc.polyfrost.oneconfig.config.elements.BasicOption;
import cc.polyfrost.oneconfig.utils.InputHandler;

import java.lang.reflect.Field;

public class DynamicDropdown extends BasicOption {
    public DynamicDropdown(Field field, Object parent, String name, String description, String category, String subcategory, int size){
        super(field, parent, name, description, category, subcategory, size);
    }


    @Override
    public void draw(long vg, int x, int y, InputHandler inputHandler) {
    }
    @Override
    public int getHeight() {
        return 32;
    }
}
