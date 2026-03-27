package com.controle_comercial.util;

import com.controle_comercial.model.entity.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public class RelatorioGenerator {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioGenerator.class);

    // === CORES UniSENAI ===
    private static final BaseColor NAVY = new BaseColor(26, 35, 50);         // #1A2332
    private static final BaseColor INDIGO = new BaseColor(99, 102, 241);     // #6366F1
    private static final BaseColor ORANGE = new BaseColor(232, 93, 4);       // #E85D04
    private static final BaseColor BG_LIGHT = new BaseColor(248, 250, 252);  // #F8FAFC
    private static final BaseColor BG_ROW = new BaseColor(241, 245, 249);    // #F1F5F9
    private static final BaseColor BORDER_LIGHT = new BaseColor(226, 232, 240);

    // === FONTES ===
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, NAVY);
    private static final Font TITLE_LIGHT = FontFactory.getFont(FontFactory.HELVETICA, 22, new BaseColor(100, 116, 139));
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, NAVY);
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, NAVY);
    private static final Font LABEL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, new BaseColor(100, 116, 139));
    private static final Font TOTAL_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, NAVY);
    private static final Font VALOR_DESTAQUE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, ORANGE);
    private static final Font EVENTO_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, INDIGO);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8, new BaseColor(148, 163, 184));
    private static final Font BADGE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.WHITE);

    // =====================================================================
    // RELATÓRIO DE EVENTOS
    // =====================================================================

    public static ByteArrayInputStream gerarRelatorioEventos(List<Evento> eventos) throws DocumentException {
        Document document = new Document(PageSize.A4, 40, 40, 50, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        adicionarCabecalhoDocumento(document, "Relatório de Eventos");
        adicionarVisaoGeral(document, eventos);

        for (Evento evento : eventos) {
            document.newPage();
            adicionarCabecalhoDocumento(document, "Detalhes do Evento");
            adicionarDetalhamentoEvento(document, evento);
        }

        adicionarRodapeTexto(document);
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    // =====================================================================
    // RELATÓRIO COMPLETO
    // =====================================================================

    public static ByteArrayInputStream gerarRelatorioCompleto(
            List<Cliente> clientes,
            List<Servico> servicos,
            List<Local> locais) throws DocumentException {

        Document document = new Document(PageSize.A4, 40, 40, 50, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // Clientes
        adicionarCabecalhoDocumento(document, "Relatório Geral - Clientes");
        PdfPTable clientesTable = criarTabela(4, new float[]{3, 2, 3, 2});
        adicionarCabecalhoTabela(clientesTable, "Nome", "Telefone", "Email", "Documento");
        for (Cliente c : clientes) {
            adicionarLinhaTabela(clientesTable, c.getNome(),
                    valorOuTraco(c.getTelefone()), valorOuTraco(c.getEmail()), valorOuTraco(c.getDocumento()));
        }
        aplicarZebra(clientesTable);
        document.add(clientesTable);

        // Servicos
        document.newPage();
        adicionarCabecalhoDocumento(document, "Relatório Geral - Serviços");
        PdfPTable servicosTable = criarTabela(3, new float[]{3, 4, 2});
        adicionarCabecalhoTabela(servicosTable, "Nome", "Descrição", "Preço Unitário");
        for (Servico s : servicos) {
            adicionarLinhaTabela(servicosTable, s.getNome(),
                    valorOuTraco(s.getDescricao()), formatarMoeda(s.getPrecoUnitario()));
        }
        aplicarZebra(servicosTable);
        document.add(servicosTable);

        // Locais
        document.newPage();
        adicionarCabecalhoDocumento(document, "Relatório Geral - Locais");
        PdfPTable locaisTable = criarTabela(3, new float[]{3, 2, 2});
        adicionarCabecalhoTabela(locaisTable, "Nome", "Tipo", "Capacidade");
        for (Local l : locais) {
            adicionarLinhaTabela(locaisTable, l.getNome(), l.getTipo().toString(),
                    l.getCapacidade() != null ? l.getCapacidade().toString() : "-");
        }
        aplicarZebra(locaisTable);
        document.add(locaisTable);

        adicionarRodapeTexto(document);
        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    // =====================================================================
    // COMPONENTES VISUAIS
    // =====================================================================

    private static void adicionarCabecalhoDocumento(Document document, String titulo) throws DocumentException {
        // Barra navy com logo + badge laranja
        PdfPTable headerBar = new PdfPTable(3);
        headerBar.setWidthPercentage(100);
        headerBar.setWidths(new float[]{1.2f, 3.5f, 1});
        headerBar.setSpacingAfter(4);

        // Logo
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBackgroundColor(NAVY);
        logoCell.setPadding(10);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        try {
            Image logo = Image.getInstance("src/main/resources/static/images/logo-unisenai.png");
            logo.scaleToFit(140, 36);
            logoCell.addElement(logo);
        } catch (Exception e) {
            logger.warn("Logo não encontrada, usando texto: {}", e.getMessage());
            logoCell.addElement(new Phrase("UniSENAI", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE)));
        }
        headerBar.addCell(logoCell);

        PdfPCell brandCell = new PdfPCell();
        brandCell.setBackgroundColor(NAVY);
        brandCell.setPadding(14);
        brandCell.setBorder(Rectangle.NO_BORDER);
        brandCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Paragraph brandP = new Paragraph("Sistema de Controle Comercial", FontFactory.getFont(FontFactory.HELVETICA, 10, new BaseColor(180, 190, 210)));
        brandCell.addElement(brandP);
        headerBar.addCell(brandCell);

        PdfPCell badgeCell = new PdfPCell(new Phrase("RELATÓRIO", BADGE_FONT));
        badgeCell.setBackgroundColor(ORANGE);
        badgeCell.setPadding(14);
        badgeCell.setBorder(Rectangle.NO_BORDER);
        badgeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        badgeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerBar.addCell(badgeCell);
        document.add(headerBar);

        // Linha indigo fina
        PdfPTable lineTable = new PdfPTable(1);
        lineTable.setWidthPercentage(100);
        lineTable.setSpacingAfter(16);
        PdfPCell lineCell = new PdfPCell();
        lineCell.setFixedHeight(3);
        lineCell.setBackgroundColor(INDIGO);
        lineCell.setBorder(Rectangle.NO_BORDER);
        lineTable.addCell(lineCell);
        document.add(lineTable);

        // Título do relatório com estilo refinado
        Paragraph titleParagraph = new Paragraph();
        titleParagraph.add(new Chunk(titulo, TITLE_FONT));
        titleParagraph.setSpacingBefore(6);
        titleParagraph.setSpacingAfter(6);
        document.add(titleParagraph);

        // Linha sutil abaixo do título
        LineSeparator subtleLine = new LineSeparator();
        subtleLine.setLineColor(BORDER_LIGHT);
        subtleLine.setLineWidth(1f);
        document.add(new Chunk(subtleLine));
        document.add(Chunk.NEWLINE);
    }

    private static void adicionarRodapeTexto(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        LineSeparator line = new LineSeparator();
        line.setLineColor(BORDER_LIGHT);
        document.add(new Chunk(line));

        String dataGeracao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));
        Paragraph footer = new Paragraph("Gerado em " + dataGeracao + " | UniSENAI - Sistema de Controle Comercial", FOOTER_FONT);
        footer.setSpacingBefore(6);
        document.add(footer);
    }

    private static void adicionarTituloSecao(Document document, String titulo) throws DocumentException {
        PdfPTable sectionTable = new PdfPTable(new float[]{4f, 96f});
        sectionTable.setWidthPercentage(100);
        sectionTable.setSpacingBefore(22);
        sectionTable.setSpacingAfter(10);

        // Barra lateral laranja
        PdfPCell accentCell = new PdfPCell();
        accentCell.setBackgroundColor(ORANGE);
        accentCell.setBorder(Rectangle.NO_BORDER);
        accentCell.setFixedHeight(32);
        sectionTable.addCell(accentCell);

        // Texto da seção
        PdfPCell textCell = new PdfPCell(new Phrase(titulo, SUBTITLE_FONT));
        textCell.setBackgroundColor(NAVY);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setPadding(9);
        textCell.setPaddingLeft(12);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        sectionTable.addCell(textCell);

        document.add(sectionTable);
    }

    // =====================================================================
    // VISÃO GERAL DE EVENTOS
    // =====================================================================

    private static void adicionarVisaoGeral(Document document, List<Evento> eventos) throws DocumentException {
        PdfPTable table = criarTabela(6, new float[]{3, 2, 2, 3, 3, 2});
        adicionarCabecalhoTabela(table, "Título", "Data", "Horário", "Cliente", "Local", "Valor Total");

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        for (Evento e : eventos) {
            String periodo = formatarPeriodo(e, dateFmt);
            String horario = e.getHoraInicio().format(timeFmt) + " - " + e.getHoraFim().format(timeFmt);

            adicionarLinhaTabela(table, e.getTitulo(), periodo, horario,
                    e.getCliente().getNome(), e.getLocal().getNome(), formatarMoeda(e.getValorTotal()));
        }

        aplicarZebra(table);
        document.add(table);
    }

    // =====================================================================
    // DETALHAMENTO DE EVENTO
    // =====================================================================

    private static void adicionarDetalhamentoEvento(Document document, Evento evento) throws DocumentException {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        // Card de título do evento
        PdfPTable titleCard = new PdfPTable(1);
        titleCard.setWidthPercentage(100);
        titleCard.setSpacingAfter(18);

        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setBorderWidthLeft(5);
        titleCell.setBorderColorLeft(ORANGE);
        titleCell.setPadding(16);
        titleCell.setBackgroundColor(BG_LIGHT);
        Paragraph eventTitle = new Paragraph(evento.getTitulo(), EVENTO_TITLE);
        titleCell.addElement(eventTitle);
        titleCard.addCell(titleCell);
        document.add(titleCard);

        // Informações básicas
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1, 2, 1, 2});
        infoTable.setSpacingAfter(15);

        adicionarCampoInfo(infoTable, "Data", formatarPeriodo(evento, dateFmt));
        adicionarCampoInfo(infoTable, "Horário", evento.getHoraInicio().format(timeFmt) + " - " + evento.getHoraFim().format(timeFmt));
        document.add(infoTable);

        // Valor em destaque
        PdfPTable valorTable = new PdfPTable(1);
        valorTable.setWidthPercentage(100);
        valorTable.setSpacingAfter(15);

        PdfPCell valorCell = new PdfPCell();
        valorCell.setBorder(Rectangle.BOX);
        valorCell.setBorderColor(BORDER_LIGHT);
        valorCell.setBackgroundColor(BG_LIGHT);
        valorCell.setPadding(14);
        Paragraph valorP = new Paragraph();
        valorP.add(new Chunk("Valor Total: ", BOLD_FONT));
        valorP.add(new Chunk(formatarMoeda(evento.getValorTotal()), VALOR_DESTAQUE));
        valorCell.addElement(valorP);
        valorTable.addCell(valorCell);
        document.add(valorTable);

        if (evento.getObservacoes() != null && !evento.getObservacoes().isEmpty()) {
            Paragraph obs = new Paragraph();
            obs.add(new Chunk("Observações: ", LABEL_FONT));
            obs.add(new Chunk(evento.getObservacoes(), NORMAL_FONT));
            obs.setSpacingAfter(10);
            document.add(obs);
        }

        // Seções
        adicionarSecaoCliente(document, evento.getCliente());
        adicionarSecaoLocal(document, evento.getLocal());
        adicionarSecaoServicos(document, evento.getServicos());
    }

    private static void adicionarSecaoCliente(Document document, Cliente cliente) throws DocumentException {
        adicionarTituloSecao(document, "Dados do Cliente");
        PdfPTable table = criarTabelaDetalhes();
        adicionarLinhaDetalhe(table, "Nome", cliente.getNome());
        adicionarLinhaDetalhe(table, "Telefone", valorOuTraco(cliente.getTelefone()));
        adicionarLinhaDetalhe(table, "Email", valorOuTraco(cliente.getEmail()));
        adicionarLinhaDetalhe(table, "Documento", valorOuTraco(cliente.getDocumento()));
        document.add(table);
    }

    private static void adicionarSecaoLocal(Document document, Local local) throws DocumentException {
        adicionarTituloSecao(document, "Local do Evento");
        PdfPTable table = criarTabelaDetalhes();
        adicionarLinhaDetalhe(table, "Nome", local.getNome());
        adicionarLinhaDetalhe(table, "Tipo", local.getTipo().toString());
        adicionarLinhaDetalhe(table, "Capacidade", local.getCapacidade() != null ? local.getCapacidade().toString() : "-");
        document.add(table);
    }

    private static void adicionarSecaoServicos(Document document, Set<EventoServico> servicos) throws DocumentException {
        adicionarTituloSecao(document, "Serviços Contratados");

        if (servicos.isEmpty()) {
            Paragraph p = new Paragraph("Nenhum serviço contratado para este evento.", NORMAL_FONT);
            p.setSpacingAfter(15);
            document.add(p);
            return;
        }

        PdfPTable table = criarTabela(4, new float[]{3, 1.5f, 2, 2});
        adicionarCabecalhoTabela(table, "Serviço", "Qtd", "Valor Unit.", "Subtotal");

        BigDecimal total = BigDecimal.ZERO;
        for (EventoServico es : servicos) {
            BigDecimal subtotal = es.getServico().getPrecoUnitario()
                    .multiply(new BigDecimal(es.getQuantidade()));
            total = total.add(subtotal);

            adicionarLinhaTabela(table, es.getServico().getNome(), es.getQuantidade().toString(),
                    formatarMoeda(es.getServico().getPrecoUnitario()), formatarMoeda(subtotal));
        }

        aplicarZebra(table);
        document.add(table);

        // Total em destaque
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(100);
        totalTable.setWidths(new float[]{3, 1});
        totalTable.setSpacingBefore(4);

        PdfPCell labelCell = new PdfPCell(new Phrase("TOTAL", BOLD_FONT));
        labelCell.setBorder(Rectangle.TOP);
        labelCell.setBorderColorTop(NAVY);
        labelCell.setBorderWidthTop(2);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(10);
        totalTable.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(formatarMoeda(total), VALOR_DESTAQUE));
        valueCell.setBorder(Rectangle.TOP);
        valueCell.setBorderColorTop(NAVY);
        valueCell.setBorderWidthTop(2);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(10);
        totalTable.addCell(valueCell);

        document.add(totalTable);
    }

    // =====================================================================
    // HELPERS
    // =====================================================================

    private static PdfPTable criarTabela(int colunas, float[] larguras) throws DocumentException {
        PdfPTable table = new PdfPTable(colunas);
        table.setWidthPercentage(100);
        table.setWidths(larguras);
        table.setSpacingBefore(8);
        table.setSpacingAfter(15);
        return table;
    }

    private static PdfPTable criarTabelaDetalhes() throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3});
        table.setSpacingAfter(10);
        return table;
    }

    private static void adicionarCabecalhoTabela(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(NAVY);
            cell.setPadding(10);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setMinimumHeight(28);
            table.addCell(cell);
        }
    }

    private static void adicionarLinhaTabela(PdfPTable table, String... valores) {
        for (String valor : valores) {
            PdfPCell cell = new PdfPCell(new Phrase(valor, NORMAL_FONT));
            cell.setPadding(9);
            cell.setBorder(Rectangle.BOTTOM);
            cell.setBorderColorBottom(BORDER_LIGHT);
            cell.setBorderWidthBottom(0.5f);
            cell.setMinimumHeight(26);
            table.addCell(cell);
        }
    }

    private static void adicionarLinhaDetalhe(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, LABEL_FONT));
        labelCell.setBorder(Rectangle.BOTTOM);
        labelCell.setBorderColorBottom(BORDER_LIGHT);
        labelCell.setBorderWidthBottom(0.5f);
        labelCell.setPadding(9);
        labelCell.setBackgroundColor(BG_LIGHT);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setBorderColorBottom(BORDER_LIGHT);
        valueCell.setBorderWidthBottom(0.5f);
        valueCell.setPadding(9);
        table.addCell(valueCell);
    }

    private static void adicionarCampoInfo(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, LABEL_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(8);
        labelCell.setBackgroundColor(BG_LIGHT);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, BOLD_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(8);
        valueCell.setBackgroundColor(BG_LIGHT);
        table.addCell(valueCell);
    }

    private static void aplicarZebra(PdfPTable table) {
        int headerRows = 1;
        int totalRows = table.getRows().size();
        for (int i = headerRows; i < totalRows; i++) {
            PdfPCell[] cells = table.getRow(i).getCells();
            if (cells != null && i % 2 == 0) {
                for (PdfPCell cell : cells) {
                    if (cell != null) {
                        cell.setBackgroundColor(BG_ROW);
                    }
                }
            }
        }
    }

    private static String formatarPeriodo(Evento evento, DateTimeFormatter fmt) {
        String periodo = evento.getDataInicio().format(fmt);
        if (evento.getDataFim() != null && !evento.getDataFim().equals(evento.getDataInicio())) {
            periodo += " a " + evento.getDataFim().format(fmt);
        }
        return periodo;
    }

    private static String formatarMoeda(BigDecimal valor) {
        if (valor == null) return "R$ 0,00";
        return "R$ " + String.format("%.2f", valor);
    }

    private static String valorOuTraco(String valor) {
        return (valor != null && !valor.isBlank()) ? valor : "-";
    }
}
