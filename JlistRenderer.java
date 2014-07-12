import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;


public class JlistRenderer extends JLabel implements ListCellRenderer {
	JSeparator separator;
	public final static String SEPARATOR = "SEPARATOR";
	public JlistRenderer() {
		setOpaque(true);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		separator = new JSeparator(JSeparator.HORIZONTAL);
	}
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		String str = (value == null) ? "" : value.toString();
		if (str.equals(SEPARATOR)) {
			return separator;
		}
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setFont(list.getFont());
		setText(str);
		return this;
	}
}