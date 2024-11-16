import streamlit as st
from transformers import pipeline

# Carga del modelo de IA para respuestas generales
@st.cache_resource
def cargar_modelo():
    return pipeline("text-generation", model="gpt2")  # Usa un modelo pequeño como GPT-2

modelo = cargar_modelo()

# Respuestas específicas sobre la página web
RESPUESTAS_PAGINA_WEB = {
    "productos": "Ofrecemos una amplia variedad de productos, desde celulares hasta laptops. Visítanos en la sección 'Productos'.",
    "contacto": "Puedes contactarnos al correo contacto@tuweb.com o al teléfono +51 123 456 789.",
    "horarios": "Estamos abiertos de lunes a viernes de 9 AM a 6 PM.",
    "ubicación": "Nos encontramos en Av. Siempre Viva 123, Lima, Perú.",
    "devoluciones": "Para devolver un producto, por favor revisa nuestra política de devoluciones en la sección 'Devoluciones'."
}

# Normalizar texto del usuario
def normalizar_texto(texto):
    texto = texto.lower().strip()  # Convertir a minúsculas y eliminar espacios
    return texto

# Buscar respuesta predefinida
def buscar_respuesta_predefinida(pregunta):
    for clave, respuesta in RESPUESTAS_PAGINA_WEB.items():
        if clave in pregunta:
            return respuesta
    return None

# Interfaz del Chatbot
st.title("Chatbot de Soporte")

# Historial del chat
if "historial" not in st.session_state:
    st.session_state["historial"] = []

# Entrada del usuario
pregunta_usuario = st.text_input("Escribe tu pregunta:")

if pregunta_usuario:
    # Normaliza la entrada
    pregunta_normalizada = normalizar_texto(pregunta_usuario)

    # Busca una respuesta predefinida
    respuesta = buscar_respuesta_predefinida(pregunta_normalizada)

    # Si no encuentra respuesta, usa el modelo de IA
    if not respuesta:
        salida_modelo = modelo(pregunta_usuario, max_length=50, num_return_sequences=1)
        respuesta = salida_modelo[0]["generated_text"]

    # Guardar en el historial
    st.session_state["historial"].append(("Usuario", pregunta_usuario))
    st.session_state["historial"].append(("Chatbot", respuesta))

# Mostrar el historial de conversación
for emisor, mensaje in st.session_state["historial"]:
    st.write(f"**{emisor}:** {mensaje}")
