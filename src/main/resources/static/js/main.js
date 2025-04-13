// Simulação de eventos
const eventosMockados = [
    { date: '2025-04-15', title: 'Reunião Comercial' },
    { date: '2025-04-18', title: 'Workshop de Tecnologia' },
    { date: '2025-04-21', title: 'Palestra Motivacional' }
];

const calendarGrid = document.getElementById('calendar-grid');
const currentMonthLabel = document.getElementById('current-month');
const prevMonthButton = document.getElementById('prev-month');
const nextMonthButton = document.getElementById('next-month');

const eventModal = new bootstrap.Modal(document.getElementById('eventModal')); // Inicializa o modal
const eventListElement = document.getElementById('eventList');
const detailsButton = document.getElementById('detailsButton');

let currentMonth = new Date().getMonth();
let currentYear = new Date().getFullYear();

function renderCalendar(month, year) {
    currentMonthLabel.textContent = `${['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'][month]} ${year}`;
    calendarGrid.innerHTML = '';

    const firstDayOfMonth = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    for (let i = 0; i < firstDayOfMonth; i++) {
        calendarGrid.appendChild(document.createElement('div'));
    }

    for (let day = 1; day <= daysInMonth; day++) {
        const cell = document.createElement('div');
        const cellDate = `${year}-${(month + 1).toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
        cell.className = 'calendar-cell';
        cell.innerHTML = `<span>${day}</span>`;

        const dayEvents = eventosMockados.filter(e => e.date === cellDate);
        if (dayEvents.length) {
            const indicator = document.createElement('div');
            indicator.className = 'event-indicator';
            cell.appendChild(indicator);
        }

        cell.addEventListener('click', () => {
            openModal(cellDate, dayEvents);
        });

        calendarGrid.appendChild(cell);
    }
}

function openModal(date, events) {
    eventListElement.innerHTML = events.length
        ? `<strong>Eventos para ${date}:</strong><ul>${events.map(e => `<li>${e.title}</li>`).join('')}</ul>`
        : `Nenhum evento disponível para ${date}.`;

    detailsButton.onclick = () => window.location.href = `/eventos?date=${date}`;
    eventModal.show(); // Abre o modal
}

prevMonthButton.onclick = () => {
    currentMonth = (currentMonth - 1 + 12) % 12;
    currentYear = currentMonth === 11 ? currentYear - 1 : currentYear;
    renderCalendar(currentMonth, currentYear);
};

nextMonthButton.onclick = () => {
    currentMonth = (currentMonth + 1) % 12;
    currentYear = currentMonth === 0 ? currentYear + 1 : currentYear;
    renderCalendar(currentMonth, currentYear);
};

renderCalendar(currentMonth, currentYear);