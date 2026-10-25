



import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;

public class Main extends JFrame {
    private final BaseDatos dao = new BaseDatos();
    private final JTextField codigo = new JTextField(15), nombre = new JTextField(15),
        descripcion = new JTextField(15), precio = new JTextField(15),
        cantidad = new JTextField(15), filtro = new JTextField(18);
    private final DefaultTableModel modelo = new DefaultTableModel(
        new String[]{"Código","Nombre","Descripción","Precio","Cantidad"},0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    public Main() {
        super("Sistema de Gestión de Productos - JDBC / SQLite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900,550); setLocationRelativeTo(null);
        JPanel campos = new JPanel(new GridLayout(5,2,8,8));
        campos.setBorder(BorderFactory.createTitledBorder("Datos del producto"));
        agregar(campos,"Código",codigo); agregar(campos,"Nombre",nombre);
        agregar(campos,"Descripción",descripcion); agregar(campos,"Precio",precio);
        agregar(campos,"Cantidad",cantidad);
        JPanel botones = new JPanel(new FlowLayout());
        JButton registrar = new JButton("Registrar"), actualizar = new JButton("Actualizar"),
            eliminar = new JButton("Eliminar"), limpiar = new JButton("Limpiar"),
            buscar = new JButton("Buscar"), mostrar = new JButton("Mostrar todos"),
            aleatorios = new JButton("Generar 10 aleatorios");
        for (JButton b : new JButton[]{registrar,actualizar,eliminar,limpiar}) botones.add(b);
        JPanel superior = new JPanel(new BorderLayout());
        superior.add(campos,BorderLayout.CENTER); superior.add(botones,BorderLayout.SOUTH);
        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busqueda.add(new JLabel("Código o nombre:")); busqueda.add(filtro);
        busqueda.add(buscar); busqueda.add(mostrar); busqueda.add(aleatorios);
        JPanel centro = new JPanel(new BorderLayout());
        centro.add(busqueda,BorderLayout.NORTH); centro.add(new JScrollPane(tabla),BorderLayout.CENTER);
        add(superior,BorderLayout.NORTH); add(centro,BorderLayout.CENTER);
        registrar.addActionListener(e -> ejecutar(() -> {
            Producto p = leerProducto(); dao.insertar(p); consultar(""); limpiarCampos();
            informar("Producto registrado correctamente.");
        }));
        actualizar.addActionListener(e -> ejecutar(() -> {
            String cod = codigo.getText().trim();
            if (cod.isEmpty()) throw new IllegalArgumentException("Ingrese el código.");
            BigDecimal pr = leerPrecio(); int cant = leerCantidad();
            boolean ok = dao.actualizar(cod,descripcion.getText().trim(),pr,cant);
            consultar(""); informar(ok ? "Producto actualizado." : "No existe ese código.");
        }));
        eliminar.addActionListener(e -> ejecutar(() -> {
            String cod = codigo.getText().trim();
            if (cod.isEmpty()) throw new IllegalArgumentException("Ingrese el código.");
            if (JOptionPane.showConfirmDialog(this,"¿Eliminar producto " + cod + "?",
                    "Confirmar eliminación",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                boolean ok = dao.eliminar(cod); consultar(""); limpiarCampos();
                informar(ok ? "Producto eliminado." : "No existe ese código.");
            }
        }));
        limpiar.addActionListener(e -> limpiarCampos());
        buscar.addActionListener(e -> ejecutar(() -> consultar(filtro.getText().trim())));
        mostrar.addActionListener(e -> ejecutar(() -> { filtro.setText(""); consultar(""); }));
        aleatorios.addActionListener(e -> ejecutar(() -> {
            int total = dao.generarDiezAleatorios();
            filtro.setText(""); consultar("");
            informar("Se agregaron " + total + " productos aleatorios.");
        }));
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                int r = tabla.convertRowIndexToModel(tabla.getSelectedRow());
                codigo.setText(String.valueOf(modelo.getValueAt(r,0)));
                nombre.setText(String.valueOf(modelo.getValueAt(r,1)));
                descripcion.setText(String.valueOf(modelo.getValueAt(r,2)));
                precio.setText(String.valueOf(modelo.getValueAt(r,3)));
                cantidad.setText(String.valueOf(modelo.getValueAt(r,4)));
            }
        });
        ejecutar(() -> consultar(""));
    }
    private void agregar(JPanel p,String etiqueta,JTextField campo) {
        p.add(new JLabel(etiqueta+":")); p.add(campo);
    }
    private BigDecimal leerPrecio() {
        try {
            BigDecimal p = new BigDecimal(precio.getText().trim());
            if (p.signum() < 0) throw new NumberFormatException();
            return p;
        } catch (NumberFormatException ex) { throw new IllegalArgumentException("El precio debe ser un número no negativo (use punto decimal)."); }
    }
    private int leerCantidad() {
        try {
            int c = Integer.parseInt(cantidad.getText().trim());
            if (c < 0) throw new NumberFormatException();
            return c;
        } catch (NumberFormatException ex) { throw new IllegalArgumentException("La cantidad debe ser un entero no negativo."); }
    }
    private Producto leerProducto() {
        String cod = codigo.getText().trim(), nom = nombre.getText().trim();
        if (cod.isEmpty() || nom.isEmpty()) throw new IllegalArgumentException("Código y nombre son obligatorios.");
        return new Producto(cod,nom,descripcion.getText().trim(),leerPrecio(),leerCantidad());
    }
    private void consultar(String texto) throws SQLException {
        modelo.setRowCount(0);
        for (Producto p : dao.listar(texto)) modelo.addRow(new Object[]{
            p.getCodigo(),p.getNombre(),p.getDescripcion(),p.getPrecio(),p.getCantidad()});
    }
    private void limpiarCampos() {
        for (JTextField t : new JTextField[]{codigo,nombre,descripcion,precio,cantidad}) t.setText("");
        tabla.clearSelection();
    }
    private void informar(String msg) { JOptionPane.showMessageDialog(this,msg); }
    private void ejecutar(Accion accion) {
        try { accion.realizar(); }
        catch (SQLException ex) { JOptionPane.showMessageDialog(this,"Error de base de datos: " + ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
        catch (IllegalArgumentException ex) { JOptionPane.showMessageDialog(this,ex.getMessage(),"Validación",JOptionPane.WARNING_MESSAGE); }
    }
    public static void main(String[] args) {
        try {
            BaseDatos.inicializar();
            SwingUtilities.invokeLater(() -> new Main().setVisible(true));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al iniciar SQLite: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    @FunctionalInterface private interface Accion { void realizar() throws SQLException; }
}
