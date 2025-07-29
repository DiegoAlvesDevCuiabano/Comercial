package com.controle_comercial.util;

import com.controle_comercial.model.entity.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public class RelatorioGenerator {

    private static final Font TITLE_FONT = new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK));
    private static final Font SUBTITLE_FONT = new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK));
    private static final Font HEADER_FONT = new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE));
    private static final Font NORMAL_FONT = new Font(FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK)); // Aumentado de 10 para 11
    private static final Font BOLD_FONT = new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK)); // Aumentado de 10 para 11
    private static final Font TABLE_HEADER_FONT = new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE)); // Aumentado de 10 para 11
    private static final Font TOTAL_FONT = new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK));
    private static final Font DETAIL_FONT = new Font(FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)); // Nova fonte para dados detalhados

    private static final BaseColor HEADER_BACKGROUND_COLOR = new BaseColor(59, 89, 152);
    private static final BaseColor TABLE_BACKGROUND_COLOR = new BaseColor(240, 240, 240);
    private static final BaseColor TITLE_BACKGROUND_COLOR = new BaseColor(220, 220, 220);

    public static ByteArrayInputStream gerarRelatorioEventos(List<Evento> eventos) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // Página de Visão Geral
        adicionarVisaoGeral(document, eventos);

        // Páginas de Detalhamento
        for (Evento evento : eventos) {
            document.newPage();
            adicionarDetalhamentoEvento(document, evento);
        }

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private static void adicionarVisaoGeral(Document document, List<Evento> eventos) throws DocumentException {
        // Título com borda de tabela
        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);
        titleTable.setSpacingBefore(10);
        titleTable.setSpacingAfter(20);

        PdfPCell titleCell = new PdfPCell(new Phrase("Relatório de Eventos - Visão Geral", TITLE_FONT));
        titleCell.setBorder(Rectangle.BOX);
        titleCell.setBackgroundColor(TITLE_BACKGROUND_COLOR);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setPadding(10);
        titleTable.addCell(titleCell);

        document.add(titleTable);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(30);
        table.setWidths(new float[]{3, 2, 2, 3, 3, 2}); // Ajuste das larguras das colunas

        // Cabeçalho da tabela
        String[] headers = {"Título", "Data", "Horário", "Cliente", "Local", "Valor Total"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(10); // Aumentado o padding
            cell.setMinimumHeight(25); // Altura mínima para o cabeçalho
            table.addCell(cell);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Dados dos eventos
        for (Evento evento : eventos) {
            addCellWithHeight(table, evento.getTitulo(), NORMAL_FONT, 25);
            addCellWithHeight(table, evento.getDataEvento().format(dateFormatter), NORMAL_FONT, 25);
            addCellWithHeight(table,
                    evento.getHoraInicio().format(timeFormatter) + " - " + evento.getHoraFim().format(timeFormatter),
                    NORMAL_FONT, 25);
            addCellWithHeight(table, evento.getCliente().getNome(), NORMAL_FONT, 25);
            addCellWithHeight(table, evento.getLocal().getNome(), NORMAL_FONT, 25);

            // Célula do valor total com formatação especial
            PdfPCell valorCell = new PdfPCell(new Phrase(
                    "R$ " + String.format("%.2f", evento.getValorTotal()),
                    BOLD_FONT));
            valorCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            valorCell.setPadding(8);
            valorCell.setMinimumHeight(25); // Altura padronizada
            table.addCell(valorCell);
        }

        document.add(table);
    }

    private static void adicionarDetalhamentoEvento(Document document, Evento evento) throws DocumentException {
        // Título do evento com borda de tabela
        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);
        titleTable.setSpacingBefore(10);
        titleTable.setSpacingAfter(20);

        PdfPCell titleCell = new PdfPCell(new Phrase("Detalhes do Evento: " + evento.getTitulo(), TITLE_FONT));
        titleCell.setBorder(Rectangle.BOX);
        titleCell.setBackgroundColor(TITLE_BACKGROUND_COLOR);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setPadding(10);
        titleTable.addCell(titleCell);

        document.add(titleTable);

        // Informações básicas do evento - com fonte maior
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1, 3});
        infoTable.setSpacingAfter(20);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        adicionarLinhaTabelaDetalhes(infoTable, "Data do Evento:", evento.getDataEvento().format(dateFormatter));
        adicionarLinhaTabelaDetalhes(infoTable, "Horário:",
                evento.getHoraInicio().format(timeFormatter) + " - " + evento.getHoraFim().format(timeFormatter));

        // Valor Total com destaque
        PdfPCell valorLabelCell = new PdfPCell(new Phrase("Valor Total:", BOLD_FONT));
        valorLabelCell.setBorder(Rectangle.NO_BORDER);
        valorLabelCell.setPadding(10);
        infoTable.addCell(valorLabelCell);

        PdfPCell valorCell = new PdfPCell(new Phrase("R$ " + String.format("%.2f", evento.getValorTotal()), TOTAL_FONT));
        valorCell.setBorder(Rectangle.NO_BORDER);
        valorCell.setPadding(10);
        infoTable.addCell(valorCell);

        adicionarLinhaTabelaDetalhes(infoTable, "Observações:",
                evento.getObservacoes() != null ? evento.getObservacoes() : "Nenhuma observação registrada");

        document.add(infoTable);

        // Seção de Cliente - com fonte maior
        adicionarSecaoCliente(document, evento.getCliente());

        // Seção de Local - com fonte maior
        adicionarSecaoLocal(document, evento.getLocal());

        // Seção de Serviços - com altura de linha padronizada
        adicionarSecaoServicos(document, evento.getServicos());
    }

    private static void adicionarSecaoCliente(Document document, Cliente cliente) throws DocumentException {
        // Título da seção com borda de tabela
        PdfPTable sectionTitleTable = new PdfPTable(1);
        sectionTitleTable.setWidthPercentage(100);
        sectionTitleTable.setSpacingBefore(25);
        sectionTitleTable.setSpacingAfter(15);

        PdfPCell sectionTitleCell = new PdfPCell(new Phrase("Dados do Cliente", SUBTITLE_FONT));
        sectionTitleCell.setBorder(Rectangle.BOX);
        sectionTitleCell.setBackgroundColor(TITLE_BACKGROUND_COLOR);
        sectionTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        sectionTitleCell.setPadding(8);
        sectionTitleTable.addCell(sectionTitleCell);

        document.add(sectionTitleTable);

        PdfPTable clienteTable = new PdfPTable(2);
        clienteTable.setWidthPercentage(100);
        clienteTable.setWidths(new float[]{1, 3});
        clienteTable.setSpacingAfter(15);

        adicionarLinhaTabelaDetalhes(clienteTable, "Nome:", cliente.getNome());
        adicionarLinhaTabelaDetalhes(clienteTable, "Telefone:", cliente.getTelefone() != null ? cliente.getTelefone() : "-");
        adicionarLinhaTabelaDetalhes(clienteTable, "Email:", cliente.getEmail() != null ? cliente.getEmail() : "-");
        adicionarLinhaTabelaDetalhes(clienteTable, "Documento:", cliente.getDocumento() != null ? cliente.getDocumento() : "-");

        document.add(clienteTable);
    }

    private static void adicionarSecaoLocal(Document document, Local local) throws DocumentException {
        // Título da seção com borda de tabela
        PdfPTable sectionTitleTable = new PdfPTable(1);
        sectionTitleTable.setWidthPercentage(100);
        sectionTitleTable.setSpacingBefore(25);
        sectionTitleTable.setSpacingAfter(15);

        PdfPCell sectionTitleCell = new PdfPCell(new Phrase("Local do Evento", SUBTITLE_FONT));
        sectionTitleCell.setBorder(Rectangle.BOX);
        sectionTitleCell.setBackgroundColor(TITLE_BACKGROUND_COLOR);
        sectionTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        sectionTitleCell.setPadding(8);
        sectionTitleTable.addCell(sectionTitleCell);

        document.add(sectionTitleTable);

        PdfPTable localTable = new PdfPTable(2);
        localTable.setWidthPercentage(100);
        localTable.setWidths(new float[]{1, 3});
        localTable.setSpacingAfter(15);

        adicionarLinhaTabelaDetalhes(localTable, "Nome:", local.getNome());
        adicionarLinhaTabelaDetalhes(localTable, "Tipo:", local.getTipo().toString());
        adicionarLinhaTabelaDetalhes(localTable, "Capacidade:",
                local.getCapacidade() != null ? local.getCapacidade().toString() : "-");

        document.add(localTable);
    }

    private static void adicionarSecaoServicos(Document document, Set<EventoServico> servicos) throws DocumentException {
        // Título da seção com borda de tabela
        PdfPTable sectionTitleTable = new PdfPTable(1);
        sectionTitleTable.setWidthPercentage(100);
        sectionTitleTable.setSpacingBefore(25);
        sectionTitleTable.setSpacingAfter(15);

        PdfPCell sectionTitleCell = new PdfPCell(new Phrase("Serviços Contratados", SUBTITLE_FONT));
        sectionTitleCell.setBorder(Rectangle.BOX);
        sectionTitleCell.setBackgroundColor(TITLE_BACKGROUND_COLOR);
        sectionTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        sectionTitleCell.setPadding(8);
        sectionTitleTable.addCell(sectionTitleCell);

        document.add(sectionTitleTable);

        if (servicos.isEmpty()) {
            Paragraph noServices = new Paragraph("Nenhum serviço contratado para este evento.", NORMAL_FONT);
            noServices.setSpacingAfter(15);
            document.add(noServices);
            return;
        }

        PdfPTable servicosTable = new PdfPTable(4);
        servicosTable.setWidthPercentage(100);
        servicosTable.setWidths(new float[]{3, 2, 2, 2});
        servicosTable.setSpacingAfter(15);

        // Cabeçalho
        String[] headers = {"Serviço", "Quantidade", "Valor Unitário", "Subtotal"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(10);
            cell.setMinimumHeight(25); // Altura padronizada
            servicosTable.addCell(cell);
        }

        // Dados
        double total = 0;
        for (EventoServico es : servicos) {
            addCellWithHeight(servicosTable, es.getServico().getNome(), NORMAL_FONT, 25);
            addCellWithHeight(servicosTable, es.getQuantidade().toString(), NORMAL_FONT, 25);
            addCellWithHeight(servicosTable,
                    "R$ " + String.format("%.2f", es.getServico().getPrecoUnitario()),
                    NORMAL_FONT, 25);

            double subtotal = es.getQuantidade() * es.getServico().getPrecoUnitario();
            total += subtotal;

            PdfPCell subtotalCell = new PdfPCell(new Phrase(
                    "R$ " + String.format("%.2f", subtotal),
                    NORMAL_FONT));
            subtotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            subtotalCell.setPadding(8);
            subtotalCell.setMinimumHeight(25); // Altura padronizada
            servicosTable.addCell(subtotalCell);
        }

        // Total - ajustado para ter o mesmo tamanho
        PdfPCell emptyCell = new PdfPCell();
        emptyCell.setBorder(Rectangle.NO_BORDER);
        emptyCell.setMinimumHeight(25);
        servicosTable.addCell(emptyCell);

        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total:", BOLD_FONT));
        totalLabelCell.setColspan(2);
        totalLabelCell.setBorder(Rectangle.NO_BORDER);
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabelCell.setPadding(8);
        totalLabelCell.setMinimumHeight(25);
        servicosTable.addCell(totalLabelCell);

        PdfPCell totalValueCell = new PdfPCell(new Phrase(
                "R$ " + String.format("%.2f", total),
                TOTAL_FONT));
        totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalValueCell.setPadding(8);
        totalValueCell.setMinimumHeight(25);
        servicosTable.addCell(totalValueCell);

        document.add(servicosTable);
    }

    // Método auxiliar para adicionar células com altura definida
    private static void addCellWithHeight(PdfPTable table, String content, Font font, float height) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(8);
        cell.setMinimumHeight(height);
        table.addCell(cell);
    }

    // Método para linhas de tabela em detalhes (com fonte maior)
    private static void adicionarLinhaTabelaDetalhes(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, BOLD_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(10);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, DETAIL_FONT)); // Usando DETAIL_FONT
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(10);
        table.addCell(valueCell);
    }

    public static ByteArrayInputStream gerarRelatorioCompleto(
            List<Cliente> clientes,
            List<Servico> servicos,
            List<Local> locais) throws DocumentException {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        Paragraph title = new Paragraph("Relatório Geral", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        Paragraph clientesTitle = new Paragraph("Clientes", SUBTITLE_FONT);
        clientesTitle.setAlignment(Element.ALIGN_CENTER);
        clientesTitle.setSpacingAfter(10);
        document.add(clientesTitle);

        PdfPTable clientesTable = new PdfPTable(4);
        clientesTable.setWidthPercentage(100);
        clientesTable.setSpacingAfter(20);
        clientesTable.setWidths(new float[]{3, 2, 3, 2});
        addClientesHeader(clientesTable);
        for (Cliente cliente : clientes) {
            addClienteRow(clientesTable, cliente);
        }
        document.add(clientesTable);

        document.newPage();
        Paragraph servicosTitle = new Paragraph("Serviços", SUBTITLE_FONT);
        servicosTitle.setAlignment(Element.ALIGN_CENTER);
        servicosTitle.setSpacingAfter(10);
        document.add(servicosTitle);

        PdfPTable servicosTable = new PdfPTable(3);
        servicosTable.setWidthPercentage(100);
        servicosTable.setSpacingAfter(20);
        servicosTable.setWidths(new float[]{2, 4, 2});
        addServicosHeader(servicosTable);
        for (Servico servico : servicos) {
            addServicoRow(servicosTable, servico);
        }
        document.add(servicosTable);

        document.newPage();
        Paragraph locaisTitle = new Paragraph("Locais", SUBTITLE_FONT);
        locaisTitle.setAlignment(Element.ALIGN_CENTER);
        locaisTitle.setSpacingAfter(10);
        document.add(locaisTitle);

        PdfPTable locaisTable = new PdfPTable(3);
        locaisTable.setWidthPercentage(100);
        locaisTable.setSpacingAfter(20);
        locaisTable.setWidths(new float[]{3, 2, 2});
        addLocaisHeader(locaisTable);
        for (Local local : locais) {
            addLocalRow(locaisTable, local);
        }
        document.add(locaisTable);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private static void addTableHeader(PdfPTable table) {
        String[] headers = {"Título", "Data", "Hora Início", "Hora Fim", "Cliente", "Local"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(new BaseColor(70, 130, 180));
            cell.setPadding(8);
            cell.setPhrase(new Phrase(header, TABLE_HEADER_FONT));
            table.addCell(cell);
        }
    }

    private static void addEventoRow(PdfPTable table, Evento evento,
                                     DateTimeFormatter dateFormatter,
                                     DateTimeFormatter timeFormatter) {
        addCell(table, evento.getTitulo());
        addCell(table, evento.getDataEvento().format(dateFormatter));
        addCell(table, evento.getHoraInicio().format(timeFormatter));
        addCell(table, evento.getHoraFim().format(timeFormatter));
        addCell(table, evento.getCliente().getNome());
        addCell(table, evento.getLocal().getNome());
    }

    private static void addClientesHeader(PdfPTable table) {
        String[] headers = {"Nome", "Telefone", "Email", "Documento"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(new BaseColor(70, 130, 180));
            cell.setPadding(8);
            cell.setPhrase(new Phrase(header, TABLE_HEADER_FONT));
            table.addCell(cell);
        }
    }

    private static void addClienteRow(PdfPTable table, Cliente cliente) {
        addCell(table, cliente.getNome());
        addCell(table, cliente.getTelefone() != null ? cliente.getTelefone() : "-");
        addCell(table, cliente.getEmail() != null ? cliente.getEmail() : "-");
        addCell(table, cliente.getDocumento() != null ? cliente.getDocumento() : "-");
    }

    private static void addServicosHeader(PdfPTable table) {
        String[] headers = {"Nome", "Descrição", "Preço Unitário"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(new BaseColor(70, 130, 180));
            cell.setPadding(8);
            cell.setPhrase(new Phrase(header, TABLE_HEADER_FONT));
            table.addCell(cell);
        }
    }

    private static void addServicoRow(PdfPTable table, Servico servico) {
        addCell(table, servico.getNome());

        PdfPCell descCell = new PdfPCell(new Phrase(
                servico.getDescricao() != null ? servico.getDescricao() : "-",
                NORMAL_FONT
        ));
        descCell.setPadding(8);
        descCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
        table.addCell(descCell);

        addCell(table, "R$ " + String.format("%.2f", servico.getPrecoUnitario()));
    }

    private static void addLocaisHeader(PdfPTable table) {
        String[] headers = {"Nome", "Tipo", "Capacidade"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(new BaseColor(70, 130, 180));
            cell.setPadding(8);
            cell.setPhrase(new Phrase(header, TABLE_HEADER_FONT));
            table.addCell(cell);
        }
    }

    private static void addLocalRow(PdfPTable table, Local local) {
        addCell(table, local.getNome());
        addCell(table, local.getTipo().toString());
        addCell(table, local.getCapacidade() != null ? local.getCapacidade().toString() : "-");
    }

    private static void addCell(PdfPTable table, String content) {
        PdfPCell cell = new PdfPCell(new Phrase(content, NORMAL_FONT));
        cell.setPadding(6);
        table.addCell(cell);
    }
}