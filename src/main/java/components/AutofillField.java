package components.autofill;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JTextField;
import javax.swing.plaf.metal.MetalTextFieldUI;

public class AutofillField extends JTextField {
    
    private AutofillFieldUI textField;
    
    public AutofillField() {
        textField = new AutofillFieldUI(this);
        setUI(textField);
    }

    public void addItems(HashMap<String, Integer> items) {
        textField.setItems(items);
    }

    public void clearItems() {
        textField.getItems().clear();
    }
    
    public List<String> getItems() {
        return textField.getItems();
    }
    
    public int getCurrentValue() {
        return textField.getCurrentValue();
    }
    
    public void setCurrentValue(int newValue) {
        textField.setItemValue(newValue, true);
    }  
}

class AutofillFieldUI extends MetalTextFieldUI {
    private JTextField textField;
    private int currentItemValue;
    private List<String> suggestions = new ArrayList<>();
    private HashMap<String, Integer> listHash;
    private State state = State.NOT_AUTOFILLED;
    private List<String> autofillResults;
    private int currentAutofillIndex = 0;
    
    private enum State { AUTOFILLED, NOT_AUTOFILLED };
    private List<Integer> blacklistedKeys = new ArrayList<>();
    
    public AutofillFieldUI(JTextField textField) {
        
        blacklistedKeys.add(KeyEvent.VK_CONTROL);
        blacklistedKeys.add(KeyEvent.VK_SHIFT);
        blacklistedKeys.add(KeyEvent.VK_CAPS_LOCK);
        blacklistedKeys.add(KeyEvent.VK_NUM_LOCK);
        blacklistedKeys.add(KeyEvent.VK_SCROLL_LOCK);
        blacklistedKeys.add(KeyEvent.VK_LEFT);
        blacklistedKeys.add(KeyEvent.VK_RIGHT);
        
        this.textField = textField;
        currentItemValue = -1;
        
        textField.addKeyListener(new KeyListener() {
            
            @Override
            public void keyTyped(KeyEvent e) {  }

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {

                if (blacklistedKeys.contains(e.getKeyCode())) return;

                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    shiftAutofill(1);
                    return;
                }
                
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    shiftAutofill(-1);
                    return;
                }
                
                String typed = textField.getText();
                if (typed.length() == 0) {
                    clearItemValue();
                    return;
                }
                
                if (listHash.get(typed) != null) {
                    setItemValue(listHash.get(typed));
                    return;
                }
                
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    clearItemValue();
                    return;
                }
                
                if (e.getKeyCode() == KeyEvent.VK_A && e.isControlDown()) {
                    textField.selectAll();
                    clearItemValue();
                    return;
                }
                
                Stream<String> filtered = suggestions.stream().filter(s ->
                    s.toLowerCase().replaceAll("[\\s+-]","").startsWith(typed.toLowerCase().replaceAll("[\\s+-]",""))
                );
                autofillResults = filtered.collect(Collectors.toList());
                if (autofillResults.isEmpty()) {
                    clearItemValue();
                    return;
                }
                
                String displayResult = autofillResults.getFirst();
                
                textField.setText(displayResult);
                textField.setCaretPosition(typed.length());
                textField.select(typed.length(), displayResult.length());
                setItemValue(listHash.get(displayResult));
            }    
        });
    }
    
    private void shiftAutofill(int direction) {
        if (autofillResults == null) return;
        
        currentAutofillIndex = (currentAutofillIndex + direction) % autofillResults.size();
        textField.setText(autofillResults.get(currentAutofillIndex));
        setItemValue(listHash.get(textField.getText()));
    }
    
    private void clearItemValue() {
        currentItemValue = -1;
        currentAutofillIndex = 0;
        autofillResults = null;
        state = State.NOT_AUTOFILLED;
    }
    
    private void setItemValue(int newValue) {
        currentItemValue = newValue;
        state = State.AUTOFILLED;
    }
    
    public void setItemValue(int newValue, boolean override) {
        setItemValue(newValue);
        
        for (HashMap.Entry<String, Integer> item : listHash.entrySet())
            if (item.getValue() == newValue) {
                textField.setText(item.getKey());
                textField.setCaretPosition(textField.getText().length());
                break;
            }
    }
    
    public int getCurrentValue() {
        return currentItemValue;
    }
    
    public List<String> getItems() {
        return this.suggestions;
    }

    public void setItems(HashMap<String, Integer> itemsHash) {
        for (String itemName : itemsHash.keySet())
            suggestions.add(itemName);
        
        Collections.sort(suggestions);
        this.listHash = itemsHash;
    }
    
    public void addItem(String item) {
        this.suggestions.add(item);
        Collections.sort(this.suggestions);
    }
}