package de.objektkontor.wsc.container;

import static java.lang.String.format;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Pipeline {

    private final String name;

    private final LinkedList<Handler> handlers = new LinkedList<>();

    public Pipeline(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public void init(ChannelPipeline channelPipeline) {
        for (Handler handler : handlers) {
            ChannelHandler channelHandler = handler.create();
            if (channelHandler != null)
                channelPipeline.addLast(channelHandler);
        }
    }

    public void addFirst(Handler handler) {
        handlers.addFirst(handler);
    }

    public void addLast(Handler handler) {
        handlers.addLast(handler);
    }

    public Handler removeHandler(String name) {
        Iterator<Handler> i = handlers.iterator();
        while (i.hasNext()) {
            Handler handler = i.next();
            if (handler.name().equals(name)) {
                i.remove();
                return handler;
            }
        }
        return null;
    }

    public SplittedPipeline split() {
        return new SplittedPipeline();
    }

    public void validate() throws Exception {
        SplittedPipeline splittedPipeline = split();
        List<PipelineValidationError> errors = new LinkedList<>();
        validateInboundHandlers(splittedPipeline.inboundHandlers(), errors);
        validateOutboundHandlers(splittedPipeline.outboundHandlers(), errors);
        if (errors.size() > 0)
            throw new InvalidPipelineException(errors);
    }

    private void validateInboundHandlers(List<InboundHandler> hadlers, List<PipelineValidationError> errors) {
        for (int i = 0; i < hadlers.size(); i++) {
            InboundHandler handler = hadlers.get(i);
            if (i < handlers.size() - 2)
                if (handler.outputInboundType() == null)
                    errors.add(new PipelineValidationError(handler, "Consuming handler allowed only at end of pipeline"));
            if (i > 1) {
                boolean matches = false;
                Class<?> inputType = handler.inputInboundType();
                for (int j = i - 1; j > 0; j--) {
                    InboundHandler previousHandler = hadlers.get(j);
                    Class<?> outputType = previousHandler.outputInboundType();
                    if (inputType.isAssignableFrom(outputType)) {
                        matches = true;
                        break;
                    }
                }
                if (!matches)
                    errors.add(new PipelineValidationError(handler, "Handler's input message type is not compatible with output message type of any previous handlers"));
            }
            if (i == handlers.size() - 1)
                if (handler.outputInboundType() != null)
                    errors.add(new PipelineValidationError(handler, "Pipeline must end with a consuming handler"));
        }
    }

    private void validateOutboundHandlers(List<OutboundHandler> hadlers, List<PipelineValidationError> errors) {
        for (int i = 0; i < hadlers.size(); i++) {
            OutboundHandler handler = hadlers.get(i);
            if (handler.outputOutboundType() == null)
                errors.add(new PipelineValidationError(handler, "Consuming handler is not allowed in output pipeline"));
            if (i > 1) {
                boolean matches = false;
                Class<?> inputType = handler.inputOutboundType();
                for (int j = i - 1; j > 0; j--) {
                    OutboundHandler previousHandler = hadlers.get(j);
                    Class<?> outputType = previousHandler.outputOutboundType();
                    if (inputType.isAssignableFrom(outputType)) {
                        matches = true;
                        break;
                    }
                }
                if (!matches)
                    errors.add(new PipelineValidationError(handler, "Handler's input message type is not compatible with output message type of any previous handlers"));
            }
        }
    }

    @Override
    public String toString() {
        SplittedPipeline splittedPipeline = split();
        StringBuilder buffer = new StringBuilder("\n");
        buffer.append("------------------------------------------------------------------------------------------------------------------------------------------------\n");
        buffer.append(" ").append(name()).append("\n");
        buffer.append("------------------------------------------------------------------------------------------------------------------------------------------------\n");
        buffer.append(" Inbound                       Message Type(s)                         | Outbound                      Message Type(s)\n");
        buffer.append("-----------------------------------------------------------------------|------------------------------------------------------------------------\n");
        int length = Math.max(splittedPipeline.inboundHandlers().size(), splittedPipeline.outboundHandlers().size());
        for (int i = 0; i < length; i++) {
            buffer.append(format(" %-70s", i < splittedPipeline.inboundHandlers().size() ? toString(splittedPipeline.inboundHandlers().get(i)) : ""));
            buffer.append(format("| %-70s", i < splittedPipeline.outboundHandlers().size() ? toString(splittedPipeline.outboundHandlers().get(i)) : ""));
            buffer.append("\n");
        }
        return buffer.toString();
    }

    private String toString(InboundHandler handler) {
        return format("%-30s", handler.name()) + "[" +
                handler.inputInboundType().getSimpleName() +
                (handler.outputInboundType() == null ? "" : " > " + handler.outputInboundType().getSimpleName()) + "]";
    }

    private String toString(OutboundHandler handler) {
        return format("%-30s", handler.name()) + "[" +
                handler.inputOutboundType().getSimpleName() +
                (handler.outputOutboundType() == null ? "" : " > " + handler.outputOutboundType().getSimpleName()) + "]";
    }

    public class SplittedPipeline {

        private final LinkedList<InboundHandler> inboundHandlers = new LinkedList<>();
        private final LinkedList<OutboundHandler> outboundHandlers = new LinkedList<>();

        public SplittedPipeline() {
            for (Handler handler : handlers) {
                if (handler instanceof InboundHandler)
                    inboundHandlers.addLast((InboundHandler) handler);
                if (handler instanceof OutboundHandler)
                    outboundHandlers.addFirst((OutboundHandler) handler);
            }
        }

        public LinkedList<InboundHandler> inboundHandlers() {
            return inboundHandlers;
        }

        public LinkedList<OutboundHandler> outboundHandlers() {
            return outboundHandlers;
        }
    }

    public class PipelineValidationError {

        private final Handler handler;
        private final String cause;

        public PipelineValidationError(Handler handler, String cause) {
            this.handler = handler;
            this.cause = cause;
        }

        public Handler getHandler() {
            return handler;
        }

        public String getCause() {
            return cause;
        }

        @Override
        public String toString() {
            return handler.name() + ": " + cause + "]";
        }
    }

    public class InvalidPipelineException extends Exception {

        private static final long serialVersionUID = -3567200380068538023L;

        private final List<PipelineValidationError> errors;

        public InvalidPipelineException(List<PipelineValidationError> errors) {
            this.errors = errors;
        }

        @Override
        public String getMessage() {
            StringBuilder buffer = new StringBuilder("Invalid pipeline:\n");
            for (PipelineValidationError error : errors)
                buffer.append(" - ").append(error).append("\n");
            buffer.append("Pipeline dump: ").append(Pipeline.this);
            return buffer.toString();
        }
    }
}
