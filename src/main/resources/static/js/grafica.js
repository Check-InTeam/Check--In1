document.addEventListener("DOMContentLoaded", function () {
    const canvas = document.getElementById("fallasChart");
    if (!canvas) return;

    const fallas = parseInt(canvas.dataset.fallas) || 0;
    const maxFallas = 10;

    const porcentaje = Math.round((fallas / maxFallas) * 100);
    const restante = maxFallas - fallas;

    const ctx = canvas.getContext("2d");

    // ============================
    //   PLUGIN PARA MOSTRAR TEXTO
    // ============================
    const centerTextPlugin = {
        id: "centerText",
        afterDraw(chart) {
            const { ctx, chartArea: { width, height } } = chart;

            ctx.save();
            ctx.font = "bold 22px sans-serif";
            ctx.fillStyle = "#000";
            ctx.textAlign = "center";
            ctx.textBaseline = "middle";

            const x = chart.getDatasetMeta(0).data[0].x;
            const y = chart.getDatasetMeta(0).data[0].y + 10; // pequeño ajuste para centrar

            ctx.fillText(porcentaje + "%", x, y);
            ctx.restore();
        }
    };

    // Registrar plugin
    Chart.register(centerTextPlugin);

    // ============================
    //       GRÁFICA
    // ============================
    new Chart(ctx, {
        type: "doughnut",
        data: {
            labels: ["Fallas", "Restante"],
            datasets: [{
                data: [fallas, restante],
                backgroundColor: ["#ef4444", "rgba(67,184,44,0.5)"], // verde transparente
                borderWidth: 0,
                circumference: 180, // semicirculo
                rotation: 270,      // que empiece arriba
                cutout: "70%",
            }]
        },
        options: {
            responsive: false,
            plugins: { legend: { display: false } }
        }
    });
});
