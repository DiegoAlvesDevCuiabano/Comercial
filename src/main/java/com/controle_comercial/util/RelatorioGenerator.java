package com.controle_comercial.util;

import com.controle_comercial.model.entity.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
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

    // === CORES ===
    private static final BaseColor NAVY = new BaseColor(26, 35, 50);
    private static final BaseColor INDIGO = new BaseColor(99, 102, 241);
    private static final BaseColor ORANGE = new BaseColor(232, 93, 4);
    private static final BaseColor BG_LIGHT = new BaseColor(248, 250, 252);
    private static final BaseColor BG_ROW = new BaseColor(241, 245, 249);
    private static final BaseColor BORDER = new BaseColor(229, 231, 235);   // #E5E7EB
    private static final BaseColor RED = new BaseColor(220, 38, 38);

    // === FONTES ===
    private static final Font F_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, NAVY);
    private static final Font F_SECTION = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
    private static final Font F_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
    private static final Font F_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10, NAVY);
    private static final Font F_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, NAVY);
    private static final Font F_LABEL = FontFactory.getFont(FontFactory.HELVETICA, 9, new BaseColor(100, 116, 139));
    private static final Font F_TOTAL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, NAVY);
    private static final Font F_VALOR = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, ORANGE);
    private static final Font F_EVENT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, INDIGO);
    private static final Font F_FOOTER = FontFactory.getFont(FontFactory.HELVETICA, 8, new BaseColor(148, 163, 184));
    private static final Font F_BADGE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.WHITE);
    private static final Font F_BRAND = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE);
    private static final Font F_BRAND_SUB = FontFactory.getFont(FontFactory.HELVETICA, 9, new BaseColor(160, 175, 200));

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // =====================================================================
    // DOCUMENTO BASE
    // =====================================================================

    private static PdfWriter criarDocumento(Document document, ByteArrayOutputStream out) throws DocumentException {
        return PdfWriter.getInstance(document, out);
    }

    private static Document novoDocumento() {
        return new Document(PageSize.A4, 30, 30, 35, 45);
    }

    private static void cabecalho(Document document, String titulo, String badge) throws DocumentException {
        // Barra navy: UniSENAI grande + sub pequeno | badge
        PdfPTable bar = new PdfPTable(2);
        bar.setWidthPercentage(100);
        bar.setWidths(new float[]{4, 1});
        bar.setSpacingAfter(3);

        PdfPCell brandCell = celula(null, NAVY, 10);
        Paragraph brandP = new Paragraph();
        brandP.add(new Chunk("UniSENAI\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.WHITE)));
        brandP.add(new Chunk("Sistema de Controle Comercial", FontFactory.getFont(FontFactory.HELVETICA, 8, new BaseColor(140, 155, 180))));
        brandCell.addElement(brandP);
        bar.addCell(brandCell);

        PdfPCell badgeCell = new PdfPCell(new Phrase(badge, F_BADGE));
        badgeCell.setBackgroundColor(ORANGE);
        badgeCell.setPadding(10);
        badgeCell.setBorder(Rectangle.NO_BORDER);
        badgeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        badgeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        bar.addCell(badgeCell);
        document.add(bar);

        // Linha indigo
        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        line.setSpacingAfter(10);
        PdfPCell lineCell = new PdfPCell();
        lineCell.setFixedHeight(3);
        lineCell.setBackgroundColor(INDIGO);
        lineCell.setBorder(Rectangle.NO_BORDER);
        line.addCell(lineCell);
        document.add(line);

        // Título do relatório
        Paragraph p = new Paragraph(titulo, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, NAVY));
        p.setSpacingAfter(4);
        document.add(p);

        // Linha sutil
        LineSeparator sep = new LineSeparator();
        sep.setLineColor(BORDER);
        sep.setLineWidth(0.5f);
        document.add(new Chunk(sep));
        document.add(Chunk.NEWLINE);
    }

    private static void rodape(PdfWriter writer, Document document) {
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));
        Phrase phrase = new Phrase("Gerado em " + data + " | UniSENAI - Sistema de Controle Comercial", F_FOOTER);
        float x = (document.left() + document.right()) / 2;
        float y = document.bottom() - 18;
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, phrase, x, y, 0);
    }

    private static void secao(Document document, String titulo) throws DocumentException {
        PdfPTable t = new PdfPTable(new float[]{3f, 97f});
        t.setWidthPercentage(100);
        t.setSpacingBefore(14);
        t.setSpacingAfter(6);

        PdfPCell accent = new PdfPCell();
        accent.setBackgroundColor(INDIGO);
        accent.setBorder(Rectangle.NO_BORDER);
        accent.setFixedHeight(22);
        t.addCell(accent);

        PdfPCell text = new PdfPCell(new Phrase(titulo, F_SECTION));
        text.setBackgroundColor(NAVY);
        text.setBorder(Rectangle.NO_BORDER);
        text.setPadding(5);
        text.setPaddingLeft(10);
        text.setVerticalAlignment(Element.ALIGN_MIDDLE);
        t.addCell(text);

        document.add(t);
    }

    private static void separador(Document document) throws DocumentException {
        LineSeparator sep = new LineSeparator();
        sep.setLineColor(BORDER);
        sep.setLineWidth(0.5f);
        sep.setPercentage(100);
        Chunk c = new Chunk(sep);
        Paragraph p = new Paragraph(c);
        p.setSpacingBefore(6);
        p.setSpacingAfter(2);
        document.add(p);
    }

    // =====================================================================
    // HELPERS DE CÉLULA E TABELA
    // =====================================================================

    private static PdfPCell celula(Phrase phrase, BaseColor bg, float padding) {
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBackgroundColor(bg);
        cell.setPadding(padding);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private static PdfPTable tabelaDetalhes() throws DocumentException {
        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        t.setWidths(new float[]{1, 3});
        t.setSpacingAfter(4);
        return t;
    }

    private static void linhaDetalhe(PdfPTable table, String label, String value) {
        PdfPCell lbl = new PdfPCell(new Phrase(label, F_LABEL));
        lbl.setBorder(Rectangle.BOTTOM);
        lbl.setBorderColorBottom(BORDER);
        lbl.setBorderWidthBottom(0.5f);
        lbl.setPadding(7);
        lbl.setBackgroundColor(BG_LIGHT);
        table.addCell(lbl);

        PdfPCell val = new PdfPCell(new Phrase(value, F_NORMAL));
        val.setBorder(Rectangle.BOTTOM);
        val.setBorderColorBottom(BORDER);
        val.setBorderWidthBottom(0.5f);
        val.setPadding(7);
        table.addCell(val);
    }

    private static PdfPTable tabelaDados(int cols, float[] widths) throws DocumentException {
        PdfPTable t = new PdfPTable(cols);
        t.setWidthPercentage(100);
        t.setWidths(widths);
        t.setSpacingBefore(4);
        t.setSpacingAfter(8);
        return t;
    }

    private static void cabecalhoTabela(PdfPTable table, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, F_HEADER));
            cell.setBackgroundColor(NAVY);
            cell.setPadding(8);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setMinimumHeight(24);
            table.addCell(cell);
        }
    }

    private static void linhaTabela(PdfPTable table, String... valores) {
        for (String v : valores) {
            PdfPCell cell = new PdfPCell(new Phrase(v, F_NORMAL));
            cell.setPadding(7);
            cell.setBorder(Rectangle.BOTTOM);
            cell.setBorderColorBottom(BORDER);
            cell.setBorderWidthBottom(0.5f);
            cell.setMinimumHeight(22);
            table.addCell(cell);
        }
    }

    private static void zebra(PdfPTable table) {
        for (int i = 1; i < table.getRows().size(); i++) {
            PdfPCell[] cells = table.getRow(i).getCells();
            if (cells != null && i % 2 == 0) {
                for (PdfPCell cell : cells) {
                    if (cell != null) cell.setBackgroundColor(BG_ROW);
                }
            }
        }
    }

    private static void campoInfo(PdfPTable table, String label, String value) {
        PdfPCell lbl = celula(new Phrase(label, F_LABEL), BG_LIGHT, 6);
        table.addCell(lbl);
        PdfPCell val = celula(new Phrase(value, F_BOLD), BG_LIGHT, 6);
        table.addCell(val);
    }

    private static String moeda(BigDecimal v) {
        if (v == null) return "R$ 0,00";
        return "R$ " + String.format("%.2f", v);
    }

    private static void blocoTotal(Document document, String label, String valor) throws DocumentException {
        separador(document);
        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(50);
        t.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.setSpacingBefore(4);

        PdfPCell lbl = new PdfPCell(new Phrase(label, F_TOTAL));
        lbl.setBorder(Rectangle.NO_BORDER);
        lbl.setPadding(10);
        lbl.setHorizontalAlignment(Element.ALIGN_RIGHT);
        lbl.setBackgroundColor(BG_LIGHT);
        t.addCell(lbl);

        PdfPCell val = new PdfPCell(new Phrase(valor, F_VALOR));
        val.setBorder(Rectangle.NO_BORDER);
        val.setPadding(10);
        val.setHorizontalAlignment(Element.ALIGN_RIGHT);
        val.setBackgroundColor(BG_LIGHT);
        t.addCell(val);

        document.add(t);
    }

    private static String nulo(String v) {
        return (v != null && !v.isBlank()) ? v : "-";
    }

    private static String periodo(Evento e) {
        String p = e.getDataInicio().format(DATE_FMT);
        if (e.getDataFim() != null && !e.getDataFim().equals(e.getDataInicio()))
            p += " a " + e.getDataFim().format(DATE_FMT);
        return p;
    }

    // =====================================================================
    // RELATÓRIO DE EVENTOS
    // =====================================================================

    public static ByteArrayInputStream gerarRelatorioEventos(List<Evento> eventos) throws DocumentException {
        Document doc = novoDocumento();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = criarDocumento(doc, out);
        doc.open();

        cabecalho(doc, "Relatório de Eventos", "RELATÓRIO");

        // Visão geral
        PdfPTable table = tabelaDados(6, new float[]{3, 2, 2, 3, 3, 2});
        cabecalhoTabela(table, "Título", "Data", "Horário", "Cliente", "Local", "Valor");
        for (Evento e : eventos) {
            linhaTabela(table, e.getTitulo(), periodo(e),
                    e.getHoraInicio().format(TIME_FMT) + "-" + e.getHoraFim().format(TIME_FMT),
                    e.getCliente().getNome(), e.getLocal().getNome(), moeda(e.getValorTotal()));
        }
        zebra(table);
        doc.add(table);

        // Detalhamento por evento
        for (Evento evento : eventos) {
            doc.newPage();
            cabecalho(doc, "Detalhes do Evento", "RELATÓRIO");

            // Título do evento
            PdfPTable titleCard = new PdfPTable(1);
            titleCard.setWidthPercentage(100);
            titleCard.setSpacingAfter(12);
            PdfPCell tc = new PdfPCell();
            tc.setBorder(Rectangle.NO_BORDER);
            tc.setBorderWidthLeft(4);
            tc.setBorderColorLeft(ORANGE);
            tc.setPadding(12);
            tc.setBackgroundColor(BG_LIGHT);
            tc.addElement(new Paragraph(evento.getTitulo(), F_EVENT_TITLE));
            titleCard.addCell(tc);
            doc.add(titleCard);

            // Info básica
            PdfPTable info = new PdfPTable(4);
            info.setWidthPercentage(100);
            info.setWidths(new float[]{1, 2, 1, 2});
            info.setSpacingAfter(8);
            campoInfo(info, "Data", periodo(evento));
            campoInfo(info, "Horário", evento.getHoraInicio().format(TIME_FMT) + " - " + evento.getHoraFim().format(TIME_FMT));
            doc.add(info);

            // Valor
            PdfPTable vt = new PdfPTable(1);
            vt.setWidthPercentage(100);
            vt.setSpacingAfter(8);
            PdfPCell vc = celula(null, BG_LIGHT, 12);
            vc.setBorder(Rectangle.BOX);
            vc.setBorderColor(BORDER);
            Paragraph vp = new Paragraph();
            vp.add(new Chunk("Valor Total: ", F_BOLD));
            vp.add(new Chunk(moeda(evento.getValorTotal()), F_VALOR));
            vc.addElement(vp);
            vt.addCell(vc);
            doc.add(vt);

            if (evento.getObservacoes() != null && !evento.getObservacoes().isBlank()) {
                Paragraph obs = new Paragraph();
                obs.add(new Chunk("Observações: ", F_LABEL));
                obs.add(new Chunk(evento.getObservacoes(), F_NORMAL));
                obs.setSpacingAfter(6);
                doc.add(obs);
            }

            separador(doc);

            // Cliente
            secao(doc, "Dados do Cliente");
            PdfPTable ct = tabelaDetalhes();
            linhaDetalhe(ct, "Nome", evento.getCliente().getNome());
            linhaDetalhe(ct, "Telefone", nulo(evento.getCliente().getTelefone()));
            linhaDetalhe(ct, "Email", nulo(evento.getCliente().getEmail()));
            linhaDetalhe(ct, "Documento", nulo(evento.getCliente().getDocumento()));
            doc.add(ct);

            // Local
            secao(doc, "Local do Evento");
            PdfPTable lt = tabelaDetalhes();
            linhaDetalhe(lt, "Nome", evento.getLocal().getNome());
            linhaDetalhe(lt, "Tipo", evento.getLocal().getTipo().toString());
            linhaDetalhe(lt, "Capacidade", evento.getLocal().getCapacidade() != null ? evento.getLocal().getCapacidade().toString() : "-");
            if (evento.getLocaisAdicionais() != null && !evento.getLocaisAdicionais().isBlank())
                linhaDetalhe(lt, "Locais Extras", evento.getLocaisAdicionais());
            doc.add(lt);

            // Serviços
            secao(doc, "Serviços Contratados");
            if (evento.getServicos() != null && !evento.getServicos().isEmpty()) {
                PdfPTable st = tabelaDados(4, new float[]{3, 1.5f, 2, 2});
                cabecalhoTabela(st, "Serviço", "Qtd", "Valor Unit.", "Subtotal");
                BigDecimal total = BigDecimal.ZERO;
                for (EventoServico es : evento.getServicos()) {
                    BigDecimal sub = es.getServico().getPrecoUnitario().multiply(new BigDecimal(es.getQuantidade()));
                    total = total.add(sub);
                    linhaTabela(st, es.getServico().getNome(), es.getQuantidade().toString(), moeda(es.getServico().getPrecoUnitario()), moeda(sub));
                }
                zebra(st);
                doc.add(st);

                blocoTotal(doc, "TOTAL:", moeda(total));
            } else {
                doc.add(new Paragraph("Nenhum serviço contratado.", F_NORMAL));
            }
        }

        rodape(writer, doc);
        doc.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    // =====================================================================
    // RELATÓRIO COMPLETO (CADASTROS)
    // =====================================================================

    public static ByteArrayInputStream gerarRelatorioCompleto(
            List<Cliente> clientes, List<Servico> servicos, List<Local> locais) throws DocumentException {

        Document doc = novoDocumento();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = criarDocumento(doc, out);
        doc.open();

        // Clientes
        cabecalho(doc, "Relatório Geral - Clientes", "RELATÓRIO");
        PdfPTable ct = tabelaDados(4, new float[]{3, 2, 3, 2});
        cabecalhoTabela(ct, "Nome", "Telefone", "Email", "Documento");
        for (Cliente c : clientes) linhaTabela(ct, c.getNome(), nulo(c.getTelefone()), nulo(c.getEmail()), nulo(c.getDocumento()));
        zebra(ct);
        doc.add(ct);

        // Serviços
        doc.newPage();
        cabecalho(doc, "Relatório Geral - Serviços", "RELATÓRIO");
        PdfPTable st = tabelaDados(3, new float[]{3, 4, 2});
        cabecalhoTabela(st, "Nome", "Descrição", "Preço Unitário");
        for (Servico s : servicos) linhaTabela(st, s.getNome(), nulo(s.getDescricao()), moeda(s.getPrecoUnitario()));
        zebra(st);
        doc.add(st);

        // Locais
        doc.newPage();
        cabecalho(doc, "Relatório Geral - Locais", "RELATÓRIO");
        PdfPTable lt = tabelaDados(3, new float[]{3, 2, 2});
        cabecalhoTabela(lt, "Nome", "Tipo", "Capacidade");
        for (Local l : locais) linhaTabela(lt, l.getNome(), l.getTipo().toString(), l.getCapacidade() != null ? l.getCapacidade().toString() : "-");
        zebra(lt);
        doc.add(lt);

        rodape(writer, doc);
        doc.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    // =====================================================================
    // RELATÓRIO DE PÚBLICO
    // =====================================================================

    public static ByteArrayInputStream gerarRelatorioPublico(List<Evento> eventos) throws DocumentException {
        Document doc = novoDocumento();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = criarDocumento(doc, out);
        doc.open();

        cabecalho(doc, "Relatório de Público Estimado", "RELATÓRIO");

        PdfPTable table = tabelaDados(5, new float[]{3, 2, 2, 2, 2});
        cabecalhoTabela(table, "Evento", "Data", "Local", "Capacidade", "Público Est.");
        int totalPublico = 0;
        for (Evento e : eventos) {
            int pub = e.getEstimativaPublico() != null ? e.getEstimativaPublico()
                    : (e.getLocal().getCapacidade() != null ? e.getLocal().getCapacidade() : 0);
            totalPublico += pub;
            linhaTabela(table, e.getTitulo(), periodo(e), e.getLocal().getNome(),
                    e.getLocal().getCapacidade() != null ? e.getLocal().getCapacidade().toString() : "-",
                    String.valueOf(pub));
        }
        zebra(table);
        doc.add(table);

        blocoTotal(doc, "Total de público estimado:", totalPublico + " pessoas");

        rodape(writer, doc);
        doc.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    // =====================================================================
    // RELATÓRIO DE HORAS
    // =====================================================================

    public static ByteArrayInputStream gerarRelatorioHoras(List<Evento> eventos) throws DocumentException {
        Document doc = novoDocumento();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = criarDocumento(doc, out);
        doc.open();

        cabecalho(doc, "Relatório de Horas Contratadas", "RELATÓRIO");

        PdfPTable table = tabelaDados(5, new float[]{3, 2, 2, 1.5f, 1.5f});
        cabecalhoTabela(table, "Evento", "Data", "Horário", "Dias", "Horas");
        double totalHoras = 0;
        for (Evento e : eventos) {
            long dias = java.time.temporal.ChronoUnit.DAYS.between(e.getDataInicio(), e.getDataFim()) + 1;
            double hpd = java.time.Duration.between(e.getHoraInicio(), e.getHoraFim()).toMinutes() / 60.0;
            double h = dias * hpd;
            totalHoras += h;
            linhaTabela(table, e.getTitulo(), periodo(e),
                    e.getHoraInicio().format(TIME_FMT) + "-" + e.getHoraFim().format(TIME_FMT),
                    String.valueOf(dias), String.format("%.1fh", h));
        }
        zebra(table);
        doc.add(table);

        blocoTotal(doc, "Total de horas contratadas:", String.format("%.1f horas", totalHoras));

        rodape(writer, doc);
        doc.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    // =====================================================================
    // PDF DE ORÇAMENTO (PROPOSTA COMERCIAL)
    // =====================================================================

    public static ByteArrayInputStream gerarPdfOrcamento(Orcamento orcamento) throws DocumentException {
        Document doc = novoDocumento();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = criarDocumento(doc, out);
        doc.open();

        // ── 1. CABEÇALHO COM HIERARQUIA ──
        // Barra navy com UniSENAI + badge PROPOSTA
        PdfPTable bar = new PdfPTable(2);
        bar.setWidthPercentage(100);
        bar.setWidths(new float[]{4, 1});
        bar.setSpacingAfter(3);

        PdfPCell brandCell = celula(null, NAVY, 10);
        Paragraph brandP = new Paragraph();
        brandP.add(new Chunk("UniSENAI\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.WHITE)));
        brandP.add(new Chunk("Sistema de Controle Comercial", FontFactory.getFont(FontFactory.HELVETICA, 8, new BaseColor(140, 155, 180))));
        brandCell.addElement(brandP);
        bar.addCell(brandCell);

        PdfPCell badgeCell = new PdfPCell(new Phrase("PROPOSTA", F_BADGE));
        badgeCell.setBackgroundColor(ORANGE);
        badgeCell.setPadding(10);
        badgeCell.setBorder(Rectangle.NO_BORDER);
        badgeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        badgeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        bar.addCell(badgeCell);
        doc.add(bar);

        // Linha indigo
        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        line.setSpacingAfter(10);
        PdfPCell lc = new PdfPCell();
        lc.setFixedHeight(3);
        lc.setBackgroundColor(INDIGO);
        lc.setBorder(Rectangle.NO_BORDER);
        line.addCell(lc);
        doc.add(line);

        // Título "Proposta Comercial" grande
        Paragraph titulo = new Paragraph("Proposta Comercial", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, NAVY));
        titulo.setSpacingAfter(2);
        doc.add(titulo);

        // Número + Validade como subtítulo
        Paragraph subInfo = new Paragraph();
        subInfo.add(new Chunk(orcamento.getNumeroOrcamento(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, INDIGO)));
        subInfo.add(new Chunk("   |   Validade: ", F_LABEL));
        subInfo.add(new Chunk(orcamento.getDataValidade() != null ? orcamento.getDataValidade().format(DATE_FMT) : "Não definida", F_BOLD));
        subInfo.setSpacingAfter(4);
        doc.add(subInfo);

        // Linha sutil
        LineSeparator sep = new LineSeparator();
        sep.setLineColor(BORDER);
        sep.setLineWidth(0.5f);
        doc.add(new Chunk(sep));

        // ── 2. CLIENTE ──
        secao(doc, "Cliente");
        PdfPTable ct = tabelaDetalhes();
        linhaDetalhe(ct, "Nome", orcamento.getCliente().getNome());
        linhaDetalhe(ct, "Telefone", nulo(orcamento.getCliente().getTelefone()));
        linhaDetalhe(ct, "Email", nulo(orcamento.getCliente().getEmail()));
        doc.add(ct);

        // Observações do cliente (discreto, itálico, só se não vazio)
        if (orcamento.getObservacoes() != null && !orcamento.getObservacoes().isBlank()) {
            Font italicSmall = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, new BaseColor(100, 116, 139));
            Paragraph obs = new Paragraph();
            obs.add(new Chunk("Obs: ", FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 9, new BaseColor(100, 116, 139))));
            obs.add(new Chunk(orcamento.getObservacoes(), italicSmall));
            obs.setSpacingBefore(2);
            obs.setSpacingAfter(4);
            doc.add(obs);
        }

        // ── 3. EVENTO PROPOSTO ──  (espaçamento via secao)
        secao(doc, "Evento Proposto");
        PdfPTable et = tabelaDetalhes();
        String dataEvento = orcamento.getDataInicioEvento().format(DATE_FMT) +
                (orcamento.getDataFimEvento().equals(orcamento.getDataInicioEvento()) ? "" : " a " + orcamento.getDataFimEvento().format(DATE_FMT));
        linhaDetalhe(et, "Data", dataEvento);
        linhaDetalhe(et, "Horário", orcamento.getHoraInicio().format(TIME_FMT) + " - " + orcamento.getHoraFim().format(TIME_FMT));
        linhaDetalhe(et, "Local", orcamento.getLocal().getNome());
        doc.add(et);

        // ── 4. SERVIÇOS ──
        secao(doc, "Serviços");
        BigDecimal subtotalServicos = BigDecimal.ZERO;
        if (orcamento.getServicos() != null && !orcamento.getServicos().isEmpty()) {
            PdfPTable st = tabelaDados(4, new float[]{3.5f, 1, 2, 2});
            cabecalhoTabela(st, "Serviço", "Qtd", "Valor Unit.", "Subtotal");

            for (OrcamentoServico os : orcamento.getServicos()) {
                BigDecimal sub = os.getServico().getPrecoUnitario().multiply(new BigDecimal(os.getQuantidade()));
                subtotalServicos = subtotalServicos.add(sub);

                // Nome e Qtd alinhados à esquerda, valores à direita
                PdfPCell nomeCell = new PdfPCell(new Phrase(os.getServico().getNome(), F_NORMAL));
                nomeCell.setPadding(7);
                nomeCell.setBorder(Rectangle.BOTTOM);
                nomeCell.setBorderColorBottom(BORDER);
                nomeCell.setBorderWidthBottom(0.5f);
                st.addCell(nomeCell);

                PdfPCell qtdCell = new PdfPCell(new Phrase(os.getQuantidade().toString(), F_NORMAL));
                qtdCell.setPadding(7);
                qtdCell.setBorder(Rectangle.BOTTOM);
                qtdCell.setBorderColorBottom(BORDER);
                qtdCell.setBorderWidthBottom(0.5f);
                qtdCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                st.addCell(qtdCell);

                PdfPCell unitCell = new PdfPCell(new Phrase(moeda(os.getServico().getPrecoUnitario()), F_NORMAL));
                unitCell.setPadding(7);
                unitCell.setBorder(Rectangle.BOTTOM);
                unitCell.setBorderColorBottom(BORDER);
                unitCell.setBorderWidthBottom(0.5f);
                unitCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                st.addCell(unitCell);

                PdfPCell subCell = new PdfPCell(new Phrase(moeda(sub), F_BOLD));
                subCell.setPadding(7);
                subCell.setBorder(Rectangle.BOTTOM);
                subCell.setBorderColorBottom(BORDER);
                subCell.setBorderWidthBottom(0.5f);
                subCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                st.addCell(subCell);
            }
            zebra(st);
            doc.add(st);
        }

        // ── 5. BLOCO DE TOTALIZAÇÃO ──
        PdfPTable totais = new PdfPTable(2);
        totais.setWidthPercentage(45);
        totais.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totais.setWidths(new float[]{2, 2});
        totais.setSpacingBefore(4);

        // Subtotal
        PdfPCell stl = new PdfPCell(new Phrase("Subtotal:", F_BOLD));
        stl.setBorder(Rectangle.BOTTOM);
        stl.setBorderColorBottom(BORDER);
        stl.setBorderWidthBottom(0.5f);
        stl.setPadding(7);
        stl.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totais.addCell(stl);

        PdfPCell stv = new PdfPCell(new Phrase(moeda(subtotalServicos), F_NORMAL));
        stv.setBorder(Rectangle.BOTTOM);
        stv.setBorderColorBottom(BORDER);
        stv.setBorderWidthBottom(0.5f);
        stv.setPadding(7);
        stv.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totais.addCell(stv);

        // Desconto (vermelho discreto, só se > 0)
        BigDecimal desconto = orcamento.getDescontoValor() != null ? orcamento.getDescontoValor() : BigDecimal.ZERO;
        if (desconto.compareTo(BigDecimal.ZERO) > 0) {
            PdfPCell dl = new PdfPCell(new Phrase("Desconto:", F_BOLD));
            dl.setBorder(Rectangle.BOTTOM);
            dl.setBorderColorBottom(BORDER);
            dl.setBorderWidthBottom(0.5f);
            dl.setPadding(7);
            dl.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totais.addCell(dl);

            PdfPCell dv = new PdfPCell(new Phrase("- " + moeda(desconto), FontFactory.getFont(FontFactory.HELVETICA, 10, RED)));
            dv.setBorder(Rectangle.BOTTOM);
            dv.setBorderColorBottom(BORDER);
            dv.setBorderWidthBottom(0.5f);
            dv.setPadding(7);
            dv.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totais.addCell(dv);
        }

        // TOTAL (destacado: bold, fonte maior, fundo highlight)
        PdfPCell tl = new PdfPCell(new Phrase("TOTAL:", F_TOTAL));
        tl.setBorder(Rectangle.TOP);
        tl.setBorderColorTop(NAVY);
        tl.setBorderWidthTop(2);
        tl.setPadding(10);
        tl.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tl.setBackgroundColor(BG_LIGHT);
        totais.addCell(tl);

        PdfPCell tv = new PdfPCell(new Phrase(moeda(orcamento.getValorTotal()), F_VALOR));
        tv.setBorder(Rectangle.TOP);
        tv.setBorderColorTop(NAVY);
        tv.setBorderWidthTop(2);
        tv.setPadding(10);
        tv.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tv.setBackgroundColor(BG_LIGHT);
        totais.addCell(tv);

        doc.add(totais);

        // ── 6. CONDIÇÕES ──
        separador(doc);
        Font condFont = FontFactory.getFont(FontFactory.HELVETICA, 8, new BaseColor(120, 130, 145));
        Paragraph cond = new Paragraph();
        cond.add(new Chunk("Condições: ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, new BaseColor(120, 130, 145))));
        cond.add(new Chunk("Pagamento via boleto bancário ou PIX. Validade conforme data indicada acima. " +
                "Valores sujeitos a alteração após o vencimento. Esta proposta não constitui contrato.", condFont));
        cond.setSpacingBefore(4);
        doc.add(cond);

        // ── 7. RODAPÉ FIXO ──
        rodape(writer, doc);

        doc.close();
        return new ByteArrayInputStream(out.toByteArray());
    }
}
