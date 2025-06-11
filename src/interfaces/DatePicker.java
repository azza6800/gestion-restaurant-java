package interfaces;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.Date;

public class DatePicker extends JPanel {
    private JSpinner dateSpinner;
    private JDialog calendarDialog;
    private Color highlightColor;
    private Color headerColor = new Color(70, 130, 180);
    private Color weekdayColor = new Color(100, 100, 100);
    private Color todayColor = new Color(255, 100, 100);
    
    public DatePicker(Color highlightColor) {
        this.highlightColor = highlightColor;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(5, 0));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        
        // Spinner avec style moderne
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(editor);
        dateSpinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Bouton calendrier avec style moderne
        JButton calendarButton = new JButton("📅");
        calendarButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        calendarButton.setPreferredSize(new Dimension(40, 30));
        calendarButton.setBackground(new Color(240, 240, 240));
        calendarButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        calendarButton.setFocusPainted(false);
        calendarButton.addActionListener(e -> showCalendar());
        
        add(dateSpinner, BorderLayout.CENTER);
        add(calendarButton, BorderLayout.EAST);
    }
    
    private void showCalendar() {
        calendarDialog = new JDialog();
        calendarDialog.setTitle("Sélection de date");
        calendarDialog.setModal(true);
        calendarDialog.setLayout(new BorderLayout());
        calendarDialog.setMinimumSize(new Dimension(300, 300));
        
        // Panel principal avec ombre
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDate());
        
        // Header avec mois/année et navigation
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(headerColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton prevMonth = new JButton("◀");
        JButton nextMonth = new JButton("▶");
        JLabel monthLabel = new JLabel(getMonthYear(cal), SwingConstants.CENTER);
        
        // Style des boutons de navigation
        for (JButton btn : new JButton[]{prevMonth, nextMonth}) {
            btn.setForeground(Color.WHITE);
            btn.setBackground(headerColor);
            btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFont(btn.getFont().deriveFont(Font.BOLD));
        }
        
        prevMonth.addActionListener(e -> {
            cal.add(Calendar.MONTH, -1);
            updateCalendar(mainPanel, cal, monthLabel);
        });
        
        nextMonth.addActionListener(e -> {
            cal.add(Calendar.MONTH, 1);
            updateCalendar(mainPanel, cal, monthLabel);
        });
        
        // Style du label mois/année
        monthLabel.setForeground(Color.WHITE);
        monthLabel.setFont(monthLabel.getFont().deriveFont(Font.BOLD, 14));
        
        headerPanel.add(prevMonth, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(nextMonth, BorderLayout.EAST);
        
        // Panel du calendrier
        JPanel calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        calendarPanel.setBackground(Color.WHITE);
        
        // Panel pour contenir le calendrier avec scrolling si nécessaire
        JScrollPane scrollPane = new JScrollPane(calendarPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.setBackground(Color.WHITE);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        calendarDialog.add(mainPanel);
        
        updateCalendar(mainPanel, cal, monthLabel);
        
        calendarDialog.pack();
        calendarDialog.setLocationRelativeTo(this);
        calendarDialog.setVisible(true);
    }
    
    private void updateCalendar(JPanel mainPanel, Calendar cal, JLabel monthLabel) {
        JPanel calendarPanel = (JPanel)((JScrollPane)((JPanel)mainPanel.getComponent(0)).getComponent(1)).getViewport().getView();
        calendarPanel.removeAll();
        
        // En-têtes des jours
        String[] days = {"Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"};
        for (String day : days) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setForeground(weekdayColor);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            calendarPanel.add(label);
        }
        
        Calendar temp = (Calendar) cal.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);
        int firstDay = temp.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = temp.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Cases vides avant le 1er jour
        for (int i = 1; i < firstDay; i++) {
            calendarPanel.add(new JLabel(""));
        }
        
        // Jours du mois
        Calendar today = Calendar.getInstance();
        Calendar selected = Calendar.getInstance();
        selected.setTime(getDate());
        
        for (int i = 1; i <= daysInMonth; i++) {
            JButton dayBtn = new JButton(String.valueOf(i));
            dayBtn.setFocusPainted(false);
            dayBtn.setContentAreaFilled(false);
            dayBtn.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            dayBtn.setOpaque(true);
            
            // Style de base
            dayBtn.setBackground(Color.WHITE);
            dayBtn.setForeground(Color.BLACK);
            
            // Style pour le week-end
            temp.set(Calendar.DAY_OF_MONTH, i);
            if (temp.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || 
                temp.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                dayBtn.setForeground(new Color(150, 150, 150));
            }
            
            // Style pour aujourd'hui
            if (i == today.get(Calendar.DAY_OF_MONTH) 
                && cal.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
                dayBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(todayColor, 2),
                    BorderFactory.createEmptyBorder(3, 0, 3, 0)
                ));
            }
            
            // Style pour la date sélectionnée
            if (i == selected.get(Calendar.DAY_OF_MONTH)
                && cal.get(Calendar.MONTH) == selected.get(Calendar.MONTH)
                && cal.get(Calendar.YEAR) == selected.get(Calendar.YEAR)) {
                dayBtn.setBackground(highlightColor);
                dayBtn.setForeground(Color.WHITE);
            }
            
            final int day = i;
            dayBtn.addActionListener(e -> {
                cal.set(Calendar.DAY_OF_MONTH, day);
                setDate(cal.getTime());
                calendarDialog.dispose();
            });
            
            // Effet hover
            dayBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (dayBtn.getBackground() != highlightColor) {
                        dayBtn.setBackground(new Color(240, 240, 240));
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (dayBtn.getBackground() != highlightColor) {
                        dayBtn.setBackground(Color.WHITE);
                    }
                }
            });
            
            calendarPanel.add(dayBtn);
        }
        
        monthLabel.setText(getMonthYear(cal));
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }
    
    private String getMonthYear(Calendar cal) {
        return String.format("%tB %tY", cal, cal).toUpperCase();
    }
    
    public Date getDate() {
        return (Date) dateSpinner.getValue();
    }
    
    public void setDate(Date date) {
        dateSpinner.setValue(date);
    }
}