package com.github.supermoonie.util;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author supermoonie
 * @since 2020/8/22
 */
public class ClipboardUtil {

    private ClipboardUtil() {

    }

    public static void copySelectionToClipboard(final TableView<?> table, List<Integer> copyColumnIndexList) {
        final Set<Integer> rows = new TreeSet<>();
        for (final TablePosition<?, ?> tablePosition : table.getSelectionModel().getSelectedCells()) {
            rows.add(tablePosition.getRow());
        }
        final StringBuilder sb = new StringBuilder();
        boolean firstRow = true;
        for (final Integer row : rows) {
            if (!firstRow) {
                sb.append('\n');
            }
            firstRow = false;
            boolean firstCol = true;
            int columnIndex = 0;
            for (final TableColumn<?, ?> column : table.getColumns()) {
                if (null != copyColumnIndexList && copyColumnIndexList.contains(columnIndex)) {
                    if (!firstCol) {
                        sb.append('\t');
                    }
                    firstCol = false;
                    final Object cellData = column.getCellData(row);
                    sb.append(cellData == null ? "" : cellData.toString());
                }
                columnIndex = columnIndex + 1;
            }
        }
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(sb.toString());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }
}
