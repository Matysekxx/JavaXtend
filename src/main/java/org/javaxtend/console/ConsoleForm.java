package org.javaxtend.console;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * A utility for creating and displaying simple forms in the console to gather user input.
 * <p>
 * This class provides a fluent API (builder pattern) to define a series of fields,
 * each with a prompt and a specific data type. It then displays the prompts sequentially,
 * reads and validates the user's input using {@link ConsoleInput}, and returns the
 * collected data in a map.
 *
 * <h3>Example of Usage:</h3>
 * <blockquote><pre>
 *     ConsoleForm form = new ConsoleForm()
 *         .addStringField("name", "Enter your full name:")
 *         .addIntegerField("age", "Enter your age:", 18, 99)
 *         .addYesNoField("subscribe", "Subscribe to our newsletter?", true);
 *
 *     Map&lt;String, Object&gt; formData = form.display();
 *
 *     String name = (String) formData.get("name");
 *     int age = (int) formData.get("age");
 *     boolean subscribed = (boolean) formData.get("subscribe");
 *
 *     System.out.println("\n--- Form Data ---");
 *     System.out.println("Name: " + name);
 *     System.out.println("Age: " + age);
 *     System.out.println("Subscribed: " + subscribed);
 * </pre></blockquote>
 */
public class ConsoleForm {

    /**
     * Represents a single field in the form. It's an abstract base class.
     */
    private abstract static class FormField {
        final String key;
        final String prompt;

        FormField(String key, String prompt) {
            this.key = key;
            this.prompt = prompt;
        }

        /**
         * Displays the prompt, reads and validates user input.
         * @return The validated value from the user.
         */
        abstract Object ask();
    }

    private static class StringField extends FormField {
        private final Predicate<String> validator;
        StringField(String key, String prompt, Predicate<String> validator) {
            super(key, prompt);
            this.validator = validator;
        }
        @Override
        Object ask() {
            return (validator != null)
                    ? ConsoleInput.readString(prompt + " ", validator)
                    : ConsoleInput.readString(prompt + " ");
        }
    }

    private static class IntegerField extends FormField {
        private final int min;
        private final int max;
        IntegerField(String key, String prompt, int min, int max) {
            super(key, prompt);
            this.min = min;
            this.max = max;
        }
        @Override
        Object ask() {
            return ConsoleInput.readInt(prompt + " ", min, max);
        }
    }

    private static class YesNoField extends FormField {
        private final boolean defaultValue;
        YesNoField(String key, String prompt, boolean defaultValue) {
            super(key, prompt);
            this.defaultValue = defaultValue;
        }
        @Override
        Object ask() {
            return ConsoleInput.readYesNo(prompt, defaultValue);
        }
    }

    private final List<FormField> fields = new ArrayList<>();

    /**
     * Adds a string input field to the form.
     * @param key The key to store the result in the map.
     * @param prompt The message to display to the user.
     * @return This ConsoleForm instance for chaining.
     */
    public ConsoleForm addStringField(String key, String prompt) {
        fields.add(new StringField(key, prompt, null));
        return this;
    }

    /**
     * Adds a validated string input field to the form.
     * @param key The key to store the result in the map.
     * @param prompt The message to display to the user.
     * @param validator A predicate to validate the input.
     * @return This ConsoleForm instance for chaining.
     */
    public ConsoleForm addStringField(String key, String prompt, Predicate<String> validator) {
        fields.add(new StringField(key, prompt, validator));
        return this;
    }

    /**
     * Adds an integer input field to the form with a specified range.
     * @param key The key to store the result in the map.
     * @param prompt The message to display to the user.
     * @param min The minimum allowed value (inclusive).
     * @param max The maximum allowed value (inclusive).
     * @return This ConsoleForm instance for chaining.
     */
    public ConsoleForm addIntegerField(String key, String prompt, int min, int max) {
        fields.add(new IntegerField(key, prompt, min, max));
        return this;
    }

    /**
     * Adds a "yes/no" confirmation field to the form.
     * @param key The key to store the result in the map.
     * @param prompt The question to ask the user.
     * @param defaultValue The default value if the user just presses Enter.
     * @return This ConsoleForm instance for chaining.
     */
    public ConsoleForm addYesNoField(String key, String prompt, boolean defaultValue) {
        fields.add(new YesNoField(key, prompt, defaultValue));
        return this;
    }

    /**
     * Displays the form to the user, field by field, and collects all the answers.
     * @return A map where keys are the field keys and values are the user's answers.
     *         The map preserves the insertion order of the fields.
     */
    public Map<String, Object> display() {
        Map<String, Object> results = new LinkedHashMap<>();
        for (FormField field : fields) {
            Object value = field.ask();
            results.put(field.key, value);
        }
        return results;
    }
}