--
-- PostgreSQL database dump
--

\restrict 3ZESq4o4a68iZgRa9LWET19sgaYFdbqytqCvXc6FzRgua8pZ3fnhitzg5q1QdmS

-- Dumped from database version 16.13 (Ubuntu 16.13-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.13 (Ubuntu 16.13-0ubuntu0.24.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: update_last_signal_column(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_last_signal_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.last_signal = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.update_last_signal_column() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: batch_status; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.batch_status (
    id integer NOT NULL,
    name character varying(20) NOT NULL,
    description character varying(100)
);


ALTER TABLE public.batch_status OWNER TO postgres;

--
-- Name: TABLE batch_status; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.batch_status IS 'Catálogo de estados para los lotes de trabajo';


--
-- Name: COLUMN batch_status.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.batch_status.name IS 'Nombre del estado (PENDING, PROCESSING, COMPLETED, FAILED)';


--
-- Name: COLUMN batch_status.description; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.batch_status.description IS 'Descripción del estado global de la petición';


--
-- Name: batch_status_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.batch_status_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.batch_status_id_seq OWNER TO postgres;

--
-- Name: batch_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.batch_status_id_seq OWNED BY public.batch_status.id;


--
-- Name: batch_transformations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.batch_transformations (
    id integer NOT NULL,
    batch_uuid character(36) NOT NULL,
    type_id integer NOT NULL,
    params jsonb DEFAULT '{}'::jsonb NOT NULL,
    execution_order integer NOT NULL
);


ALTER TABLE public.batch_transformations OWNER TO postgres;

--
-- Name: TABLE batch_transformations; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.batch_transformations IS 'Define el pipeline de transformaciones a aplicar a todas las imágenes de un batch';


--
-- Name: COLUMN batch_transformations.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.batch_transformations.id IS 'Identificador único del registro de transformación dentro del batch';


--
-- Name: COLUMN batch_transformations.batch_uuid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.batch_transformations.batch_uuid IS 'FK al lote que contiene las imágenes a procesar';


--
-- Name: COLUMN batch_transformations.type_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.batch_transformations.type_id IS 'FK al tipo de transformación a aplicar (ej. resize, grayscale)';


--
-- Name: COLUMN batch_transformations.params; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.batch_transformations.params IS 'Parámetros específicos de la transformación en formato JSONB (ej. dimensiones, calidad)';


--
-- Name: COLUMN batch_transformations.execution_order; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.batch_transformations.execution_order IS 'Orden de ejecución dentro del pipeline de transformaciones del batch';


--
-- Name: batch_transformations_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.batch_transformations_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.batch_transformations_id_seq OWNER TO postgres;

--
-- Name: batch_transformations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.batch_transformations_id_seq OWNED BY public.batch_transformations.id;


--
-- Name: batches; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.batches (
    batch_uuid character(36) NOT NULL,
    user_uuid character(36) NOT NULL,
    request_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    status_id integer NOT NULL
);


ALTER TABLE public.batches OWNER TO postgres;

--
-- Name: TABLE batches; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.batches IS 'Cabecera de las solicitudes de procesamiento';


--
-- Name: COLUMN batches.batch_uuid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.batches.batch_uuid IS 'ID del lote';


--
-- Name: COLUMN batches.user_uuid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.batches.user_uuid IS 'Referencia lógica a auth_db';


--
-- Name: COLUMN batches.request_time; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.batches.request_time IS 'Fecha de recepción';


--
-- Name: COLUMN batches.status_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.batches.status_id IS 'FK a batch_statuses';


--
-- Name: image_status; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.image_status (
    id integer NOT NULL,
    name character varying(20) NOT NULL,
    description character varying(100)
);


ALTER TABLE public.image_status OWNER TO postgres;

--
-- Name: TABLE image_status; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.image_status IS 'Catálogo de estados para el ciclo de vida de cada imagen';


--
-- Name: COLUMN image_status.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.image_status.name IS 'Nombre del estado (RECEIVED, PROCESSING, CONVERTED, FAILED)';


--
-- Name: COLUMN image_status.description; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.image_status.description IS 'Descripción del progreso individual de la imagen';


--
-- Name: image_status_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.image_status_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.image_status_id_seq OWNER TO postgres;

--
-- Name: image_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.image_status_id_seq OWNED BY public.image_status.id;


--
-- Name: images; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.images (
    image_uuid character(36) NOT NULL,
    batch_uuid character(36) NOT NULL,
    original_name character varying(255) NOT NULL,
    result_path character varying(500),
    status_id integer NOT NULL,
    node_id integer,
    reception_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    conversion_time timestamp without time zone
);


ALTER TABLE public.images OWNER TO postgres;

--
-- Name: TABLE images; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.images IS 'Trazabilidad detallada de conversión por imagen';


--
-- Name: COLUMN images.image_uuid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.images.image_uuid IS 'ID de la imagen';


--
-- Name: COLUMN images.batch_uuid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.images.batch_uuid IS 'Relación con el lote';


--
-- Name: COLUMN images.original_name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.images.original_name IS 'Nombre del archivo original';


--
-- Name: COLUMN images.result_path; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.images.result_path IS 'Ruta del resultado final';


--
-- Name: COLUMN images.status_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.images.status_id IS 'FK a image_statuses';


--
-- Name: COLUMN images.node_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.images.node_id IS 'Nodo asignado para el trabajo';


--
-- Name: COLUMN images.conversion_time; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.images.conversion_time IS 'Fecha/hora de finalización';


--
-- Name: log_levels; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.log_levels (
    id integer NOT NULL,
    name character varying(15) NOT NULL,
    description character varying(100)
);


ALTER TABLE public.log_levels OWNER TO postgres;

--
-- Name: TABLE log_levels; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.log_levels IS 'Catálogo de niveles de severidad para el sistema de logs';


--
-- Name: COLUMN log_levels.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.log_levels.name IS 'Severidad (INFO, WARNING, ERROR)';


--
-- Name: log_levels_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.log_levels_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.log_levels_id_seq OWNER TO postgres;

--
-- Name: log_levels_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.log_levels_id_seq OWNED BY public.log_levels.id;


--
-- Name: node_metrics; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.node_metrics (
    id bigint NOT NULL,
    node_id integer NOT NULL,
    image_uuid character(36),
    ram_used_mb numeric(10,2) NOT NULL,
    ram_total_mb numeric(10,2) NOT NULL,
    cpu_percent numeric(5,2) NOT NULL,
    workers_busy integer NOT NULL,
    workers_total integer NOT NULL,
    queue_size integer NOT NULL,
    queue_capacity integer NOT NULL,
    tasks_done integer NOT NULL,
    steals_performed integer NOT NULL,
    avg_latency_ms numeric(10,2),
    p95_latency_ms numeric(10,2),
    uptime_seconds bigint NOT NULL,
    status_id integer NOT NULL,
    reported_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.node_metrics OWNER TO postgres;

--
-- Name: TABLE node_metrics; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.node_metrics IS 'Historial de telemetría y salud reportado por el mensaje NodeMetrics';


--
-- Name: COLUMN node_metrics.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.id IS 'BIGSERIAL porque las métricas crecen muy rápido';


--
-- Name: COLUMN node_metrics.node_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.node_id IS 'FK a la tabla nodes (identificador relacional)';


--
-- Name: COLUMN node_metrics.image_uuid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.image_uuid IS 'FK a la tabla images (identificador relacional)';


--
-- Name: COLUMN node_metrics.ram_used_mb; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.ram_used_mb IS 'Memoria RAM utilizada en MB';


--
-- Name: COLUMN node_metrics.ram_total_mb; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.ram_total_mb IS 'Memoria RAM total en MB';


--
-- Name: COLUMN node_metrics.cpu_percent; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.cpu_percent IS 'Porcentaje de uso de CPU (0 a 100)';


--
-- Name: COLUMN node_metrics.workers_busy; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.workers_busy IS 'Cantidad de workers actualmente ocupados';


--
-- Name: COLUMN node_metrics.workers_total; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.workers_total IS 'Total de workers';


--
-- Name: COLUMN node_metrics.queue_size; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.queue_size IS 'Tareas en cola local ahora mismo';


--
-- Name: COLUMN node_metrics.queue_capacity; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.queue_capacity IS 'Capacidad de la cola';


--
-- Name: COLUMN node_metrics.tasks_done; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.tasks_done IS 'Acumulado de tareas finalizadas desde el arranque';


--
-- Name: COLUMN node_metrics.steals_performed; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.steals_performed IS 'Work-steals realizados a otros nodos';


--
-- Name: COLUMN node_metrics.avg_latency_ms; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.avg_latency_ms IS 'Media de latencia de las últimas 100 tareas en ms';


--
-- Name: COLUMN node_metrics.p95_latency_ms; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.p95_latency_ms IS 'Percentil 95 de latencia en ms';


--
-- Name: COLUMN node_metrics.uptime_seconds; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.uptime_seconds IS 'Tiempo de actividad del nodo en segundos';


--
-- Name: COLUMN node_metrics.status_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.status_id IS 'FK a node_status (IDLE, BUSY, STEALING, ERROR)';


--
-- Name: COLUMN node_metrics.reported_at; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_metrics.reported_at IS 'Timestamp exacto en que se generó la métrica';


--
-- Name: node_metrics_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.node_metrics_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.node_metrics_id_seq OWNER TO postgres;

--
-- Name: node_metrics_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.node_metrics_id_seq OWNED BY public.node_metrics.id;


--
-- Name: node_status; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.node_status (
    id integer NOT NULL,
    name character varying(20) NOT NULL,
    description character varying(100)
);


ALTER TABLE public.node_status OWNER TO postgres;

--
-- Name: TABLE node_status; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.node_status IS 'Catálogo de estados posibles para los nodos trabajadores';


--
-- Name: COLUMN node_status.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_status.name IS 'Nombre del estado (ACTIVE, INACTIVE, ERROR)';


--
-- Name: COLUMN node_status.description; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.node_status.description IS 'Descripción detallada del estado del nodo';


--
-- Name: node_status_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.node_status_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.node_status_id_seq OWNER TO postgres;

--
-- Name: node_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.node_status_id_seq OWNED BY public.node_status.id;


--
-- Name: nodes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nodes (
    id integer NOT NULL,
    node_id character varying(100) NOT NULL,
    host character varying(255) NOT NULL,
    port integer NOT NULL,
    status_id integer NOT NULL,
    last_signal timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.nodes OWNER TO postgres;

--
-- Name: TABLE nodes; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.nodes IS 'Registro y monitoreo de los nodos trabajadores';


--
-- Name: COLUMN nodes.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.nodes.id IS 'Identificador interno del registro';


--
-- Name: COLUMN nodes.node_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.nodes.node_id IS 'ID descriptivo del nodo';


--
-- Name: COLUMN nodes.host; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.nodes.host IS 'Dirección IP o dominio';


--
-- Name: COLUMN nodes.port; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.nodes.port IS 'Puerto gRPC';


--
-- Name: COLUMN nodes.status_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.nodes.status_id IS 'FK a node_statuses';


--
-- Name: COLUMN nodes.last_signal; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.nodes.last_signal IS 'Monitoreo de actividad';


--
-- Name: nodes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.nodes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.nodes_id_seq OWNER TO postgres;

--
-- Name: nodes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.nodes_id_seq OWNED BY public.nodes.id;


--
-- Name: processing_logs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.processing_logs (
    id integer NOT NULL,
    node_id integer NOT NULL,
    image_uuid character(36),
    level_id integer NOT NULL,
    message text NOT NULL,
    log_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.processing_logs OWNER TO postgres;

--
-- Name: TABLE processing_logs; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.processing_logs IS 'Registro centralizado de eventos y errores distribuidos';


--
-- Name: COLUMN processing_logs.level_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.processing_logs.level_id IS 'FK a log_levels';


--
-- Name: processing_logs_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.processing_logs_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.processing_logs_id_seq OWNER TO postgres;

--
-- Name: processing_logs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.processing_logs_id_seq OWNED BY public.processing_logs.id;


--
-- Name: transformation_types; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.transformation_types (
    id integer NOT NULL,
    name character varying(50) NOT NULL,
    price numeric(10,2) NOT NULL,
    description character varying(100)
);


ALTER TABLE public.transformation_types OWNER TO postgres;

--
-- Name: TABLE transformation_types; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE public.transformation_types IS 'Catálogo de transformaciones soportadas por los nodos Python';


--
-- Name: COLUMN transformation_types.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.transformation_types.name IS 'Nombre técnico de la transformación';


--
-- Name: COLUMN transformation_types.price; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.transformation_types.price IS 'Precio de la transformación';


--
-- Name: COLUMN transformation_types.description; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.transformation_types.description IS 'Explicación de la operación (ej. Escala de grises, Rotar)';


--
-- Name: transformation_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.transformation_types_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.transformation_types_id_seq OWNER TO postgres;

--
-- Name: transformation_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.transformation_types_id_seq OWNED BY public.transformation_types.id;


--
-- Name: batch_status id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.batch_status ALTER COLUMN id SET DEFAULT nextval('public.batch_status_id_seq'::regclass);


--
-- Name: batch_transformations id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.batch_transformations ALTER COLUMN id SET DEFAULT nextval('public.batch_transformations_id_seq'::regclass);


--
-- Name: image_status id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.image_status ALTER COLUMN id SET DEFAULT nextval('public.image_status_id_seq'::regclass);


--
-- Name: log_levels id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.log_levels ALTER COLUMN id SET DEFAULT nextval('public.log_levels_id_seq'::regclass);


--
-- Name: node_metrics id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.node_metrics ALTER COLUMN id SET DEFAULT nextval('public.node_metrics_id_seq'::regclass);


--
-- Name: node_status id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.node_status ALTER COLUMN id SET DEFAULT nextval('public.node_status_id_seq'::regclass);


--
-- Name: nodes id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nodes ALTER COLUMN id SET DEFAULT nextval('public.nodes_id_seq'::regclass);


--
-- Name: processing_logs id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.processing_logs ALTER COLUMN id SET DEFAULT nextval('public.processing_logs_id_seq'::regclass);


--
-- Name: transformation_types id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transformation_types ALTER COLUMN id SET DEFAULT nextval('public.transformation_types_id_seq'::regclass);


--
-- Name: batch_status batch_status_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.batch_status
    ADD CONSTRAINT batch_status_name_key UNIQUE (name);


--
-- Name: batch_status batch_status_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.batch_status
    ADD CONSTRAINT batch_status_pkey PRIMARY KEY (id);


--
-- Name: batch_transformations batch_transformations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.batch_transformations
    ADD CONSTRAINT batch_transformations_pkey PRIMARY KEY (id);


--
-- Name: batches batches_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.batches
    ADD CONSTRAINT batches_pkey PRIMARY KEY (batch_uuid);


--
-- Name: image_status image_status_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.image_status
    ADD CONSTRAINT image_status_name_key UNIQUE (name);


--
-- Name: image_status image_status_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.image_status
    ADD CONSTRAINT image_status_pkey PRIMARY KEY (id);


--
-- Name: images images_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT images_pkey PRIMARY KEY (image_uuid);


--
-- Name: log_levels log_levels_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.log_levels
    ADD CONSTRAINT log_levels_name_key UNIQUE (name);


--
-- Name: log_levels log_levels_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.log_levels
    ADD CONSTRAINT log_levels_pkey PRIMARY KEY (id);


--
-- Name: node_metrics node_metrics_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.node_metrics
    ADD CONSTRAINT node_metrics_pkey PRIMARY KEY (id);


--
-- Name: node_status node_status_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.node_status
    ADD CONSTRAINT node_status_name_key UNIQUE (name);


--
-- Name: node_status node_status_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.node_status
    ADD CONSTRAINT node_status_pkey PRIMARY KEY (id);


--
-- Name: nodes nodes_node_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nodes
    ADD CONSTRAINT nodes_node_id_key UNIQUE (node_id);


--
-- Name: nodes nodes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nodes
    ADD CONSTRAINT nodes_pkey PRIMARY KEY (id);


--
-- Name: processing_logs processing_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.processing_logs
    ADD CONSTRAINT processing_logs_pkey PRIMARY KEY (id);


--
-- Name: transformation_types transformation_types_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transformation_types
    ADD CONSTRAINT transformation_types_name_key UNIQUE (name);


--
-- Name: transformation_types transformation_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.transformation_types
    ADD CONSTRAINT transformation_types_pkey PRIMARY KEY (id);


--
-- Name: nodes update_nodes_last_signal; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER update_nodes_last_signal BEFORE UPDATE ON public.nodes FOR EACH ROW EXECUTE FUNCTION public.update_last_signal_column();


--
-- Name: batch_transformations batch_transformations_batch_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.batch_transformations
    ADD CONSTRAINT batch_transformations_batch_uuid_fkey FOREIGN KEY (batch_uuid) REFERENCES public.batches(batch_uuid);


--
-- Name: batch_transformations batch_transformations_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.batch_transformations
    ADD CONSTRAINT batch_transformations_type_id_fkey FOREIGN KEY (type_id) REFERENCES public.transformation_types(id);


--
-- Name: batches batches_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.batches
    ADD CONSTRAINT batches_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.batch_status(id);


--
-- Name: images images_batch_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT images_batch_uuid_fkey FOREIGN KEY (batch_uuid) REFERENCES public.batches(batch_uuid);


--
-- Name: images images_node_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT images_node_id_fkey FOREIGN KEY (node_id) REFERENCES public.nodes(id);


--
-- Name: images images_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT images_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.image_status(id);


--
-- Name: node_metrics node_metrics_image_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.node_metrics
    ADD CONSTRAINT node_metrics_image_uuid_fkey FOREIGN KEY (image_uuid) REFERENCES public.images(image_uuid);


--
-- Name: node_metrics node_metrics_node_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.node_metrics
    ADD CONSTRAINT node_metrics_node_id_fkey FOREIGN KEY (node_id) REFERENCES public.nodes(id);


--
-- Name: node_metrics node_metrics_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.node_metrics
    ADD CONSTRAINT node_metrics_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.node_status(id);


--
-- Name: nodes nodes_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nodes
    ADD CONSTRAINT nodes_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.node_status(id);


--
-- Name: processing_logs processing_logs_image_uuid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.processing_logs
    ADD CONSTRAINT processing_logs_image_uuid_fkey FOREIGN KEY (image_uuid) REFERENCES public.images(image_uuid);


--
-- Name: processing_logs processing_logs_level_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.processing_logs
    ADD CONSTRAINT processing_logs_level_id_fkey FOREIGN KEY (level_id) REFERENCES public.log_levels(id);


--
-- Name: processing_logs processing_logs_node_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.processing_logs
    ADD CONSTRAINT processing_logs_node_id_fkey FOREIGN KEY (node_id) REFERENCES public.nodes(id);


--
-- Name: mi_publicacion; Type: PUBLICATION; Schema: -; Owner: postgres
--

CREATE PUBLICATION mi_publicacion FOR ALL TABLES WITH (publish = 'insert, update, delete, truncate');


ALTER PUBLICATION mi_publicacion OWNER TO postgres;

--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

GRANT USAGE ON SCHEMA public TO enfok;


--
-- Name: TABLE batch_status; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.batch_status TO enfok;


--
-- Name: SEQUENCE batch_status_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.batch_status_id_seq TO enfok;


--
-- Name: TABLE batch_transformations; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.batch_transformations TO enfok;


--
-- Name: SEQUENCE batch_transformations_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.batch_transformations_id_seq TO enfok;


--
-- Name: TABLE batches; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.batches TO enfok;


--
-- Name: TABLE image_status; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.image_status TO enfok;


--
-- Name: SEQUENCE image_status_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.image_status_id_seq TO enfok;


--
-- Name: TABLE images; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.images TO enfok;


--
-- Name: TABLE log_levels; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.log_levels TO enfok;


--
-- Name: SEQUENCE log_levels_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.log_levels_id_seq TO enfok;


--
-- Name: TABLE node_metrics; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.node_metrics TO enfok;


--
-- Name: SEQUENCE node_metrics_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.node_metrics_id_seq TO enfok;


--
-- Name: TABLE node_status; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.node_status TO enfok;


--
-- Name: SEQUENCE node_status_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.node_status_id_seq TO enfok;


--
-- Name: TABLE nodes; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.nodes TO enfok;


--
-- Name: SEQUENCE nodes_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.nodes_id_seq TO enfok;


--
-- Name: TABLE processing_logs; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.processing_logs TO enfok;


--
-- Name: SEQUENCE processing_logs_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.processing_logs_id_seq TO enfok;


--
-- Name: TABLE transformation_types; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.transformation_types TO enfok;


--
-- Name: SEQUENCE transformation_types_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.transformation_types_id_seq TO enfok;


--
-- PostgreSQL database dump complete
--

\unrestrict 3ZESq4o4a68iZgRa9LWET19sgaYFdbqytqCvXc6FzRgua8pZ3fnhitzg5q1QdmS

