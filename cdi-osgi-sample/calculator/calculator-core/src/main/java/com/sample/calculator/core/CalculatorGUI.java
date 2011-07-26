package com.sample.calculator.core;

import com.sample.calculator.api.*;
import org.osgi.cdi.api.extension.Service;
import org.osgi.cdi.api.extension.annotation.Specification;
import org.osgi.cdi.api.extension.events.InterBundleEvent;
import org.osgi.cdi.api.extension.events.ServiceEvents;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class CalculatorGUI extends JFrame {

    private final Font BIGGER_FONT = new Font("monospaced", Font.PLAIN, 20);
    private JTextField textfield;
    private Operation currentOperation = new OperationImpl();
    private String currentValue = "";
    private String displayedValue = "";
    private @Inject Event<InterBundleEvent> ibEvent;
    private Service<Operator> operators;
    private DefaultListModel model = new DefaultListModel();
    private JPanel pan;
    private JPanel panel;
    private Map<String, OperatorListener> registeredOperators = new HashMap<String, OperatorListener>();

    @Inject
    public CalculatorGUI(Service<Operator> operators) {
        this.operators = operators;
        textfield = new JTextField("0", 12);
        textfield.setHorizontalAlignment(JTextField.RIGHT);
        textfield.setFont(BIGGER_FONT);
        textfield.setEditable(false);
        ActionListener listener = null;
        String buttonOrder = "123456789C0=";
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 3, 4, 4));
        for (int i = 0; i < buttonOrder.length(); i++) {
            String key = buttonOrder.substring(i, i + 1);
            if (key.equals("=")) {
                listener = new EqualsListener();
            } else if (key.equals("C")) {
                listener = new CleanListener();
            } else {
                listener = new NumberListener(Integer.parseInt(key));
            }
            JButton button = new JButton(key);
            button.addActionListener(listener);
            button.setFont(BIGGER_FONT);
            buttonPanel.add(button);
        }
        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 4, 4));
        JButton b = new JButton("$");
        b.setFont(BIGGER_FONT);
        b.setVisible(false);
        panel.add(b);
        for (Operator operator : operators) {
            JButton button = new JButton(operator.label());
            button.addActionListener(new OperatorListener(operator));
            button.setFont(BIGGER_FONT);
            panel.add(button);
        }
        JPanel notifs = new JPanel();
        JList list = new JList(model);
        notifs.add(list);
        model.add(0, "Start ...                ");
        pan = new JPanel();
        pan.setLayout(new BorderLayout(4, 4));
        pan.add(textfield, BorderLayout.NORTH);
        pan.add(buttonPanel, BorderLayout.CENTER);
        pan.add(panel, BorderLayout.EAST);
        pan.add(notifs, BorderLayout.WEST);
        this.setContentPane(pan);
        this.pack();
        this.setTitle("Calculator");
        this.setResizable(false);
    }

    public void bindService(@Observes @Specification(Operator.class) ServiceEvents.ServiceArrival arrival) {
        Operator o = arrival.getService(Operator.class);
        if (!registeredOperators.containsKey(o.label())) {
            registeredOperators.put(o.label(), new OperatorListener(o));
        }
        update();
    }

    public void unbindService(@Observes @Specification(Operator.class) ServiceEvents.ServiceDeparture departure) {
        Operator o = departure.getService(Operator.class);
        if (registeredOperators.containsKey(o.label())) {
            registeredOperators.remove(o.label());
        }
        update();
    }

    private void update() {
        pan.remove(panel);
        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 4, 4));
        for (OperatorListener listener : registeredOperators.values()) {
            JButton button = new JButton(listener.getOperator().label());
            button.addActionListener(listener);
            button.setFont(BIGGER_FONT);
            panel.add(button);
        }
        pan.add(panel, BorderLayout.EAST);
        validate();
        repaint();
    }

    public void listenNotifications(@Observes @Specification(NotificationEvent.class) InterBundleEvent event) {
        model.add(0, event.typed(NotificationEvent.class).get().getMessage());
    }

    public void listenToEquals(@Observes @Specification(EqualsEvent.class) InterBundleEvent event) {
        if (!currentOperation.isValue2Set()) {
            currentOperation.setValue2(Integer.parseInt(currentValue));
        }
        if (currentOperation.isValue1Set() && currentOperation.isValue2Set() && currentOperation.isOperatorSet()) {
            this.textfield.setText("" + currentOperation.value());
            displayedValue += " = " + currentOperation.value();
            ibEvent.fire(new InterBundleEvent(currentOperation, Operation.class));
        } else {
            this.textfield.setText("Error ...");
        }
        currentValue = "";
        displayedValue = "";
        this.currentOperation = new OperationImpl();
    }

    public void listenToClean(@Observes @Specification(CleanEvent.class) InterBundleEvent event) {
        this.textfield.setText("0");
        currentValue = "";
        displayedValue = "";
        this.currentOperation = new OperationImpl();
    }

    public void listenToNumbers(@Observes @Specification(NumberEvent.class) InterBundleEvent event) {
        currentValue += event.typed(NumberEvent.class).get().getValue();
        displayedValue += event.typed(NumberEvent.class).get().getValue();
        this.textfield.setText(displayedValue);
    }

    public void listenOperators(@Observes @Specification(Operator.class) InterBundleEvent event) {
        if (!currentOperation.isValue1Set()) {
            currentOperation.setValue1(Integer.parseInt(currentValue));
        }
        if (!currentOperation.isOperatorSet()) {
            currentOperation.setOperator(event.typed(Operator.class).get());
        }
        currentValue = "";
        displayedValue += " " + event.typed(Operator.class).get().label() + " ";
        this.textfield.setText(displayedValue);
    }

    private class CleanListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ibEvent.select(new SpecificationAnnotation(CleanEvent.class)).fire(new InterBundleEvent(new CleanEvent()));
        }
    }

    private class EqualsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ibEvent.select(new SpecificationAnnotation(EqualsEvent.class)).fire(new InterBundleEvent(new EqualsEvent(currentOperation)));
        }
    }

    private class NumberListener implements ActionListener {

        private final int value;

        public NumberListener(int value) {
            this.value = value;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            ibEvent.select(new SpecificationAnnotation(NumberEvent.class)).fire(new InterBundleEvent(new NumberEvent(value)));
        }
    }

    private class OperatorListener implements ActionListener {

        private final Operator operator;

        public OperatorListener(Operator operator) {
            this.operator = operator;
        }

        public Operator getOperator() {
            return operator;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ibEvent.select(new SpecificationAnnotation(Operator.class)).fire(new InterBundleEvent(operator, Operator.class));
        }
    }

    public static class SpecificationAnnotation
            extends AnnotationLiteral<Specification>
            implements Specification {

        private final Class value;

        public SpecificationAnnotation(Class value) {
            this.value = value;
        }

        @Override
        public Class value() {
            return value;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Specification.class;
        }
    }
}
