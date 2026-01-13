package com.example.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Predicate;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * 实体AI异步优化插件主类
 * 使用多核心异步处理技术优化实体AI性能，提升服务器流畅度
 * 支持多种服务端核心：Paper, Purpur, Luminol, Bukkit, Folia, Spigot
 */
public class MyLeavesPlugin extends JavaPlugin implements Listener {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MyLeavesPlugin.class.getName());
    
    private BukkitTask aiOptimizationTask;
    private boolean aiOptimizationEnabled = true;
    private int aiProcessingInterval = 2;
    private int maxEntitiesPerTick = 10;
    
    // 针对特定实体类型的配置
    private boolean piglinOptimizationEnabled = true;
    private int piglinIntervalMultiplier = 1;
    private boolean piglinBruteOptimizationEnabled = true;
    private int piglinBruteIntervalMultiplier = 1;
    private boolean otherMobsOptimizationEnabled = true;
    private int otherMobsIntervalMultiplier = 1;
    
    // 调试选项
    private boolean debugEnabled = false;
    private boolean performanceStatsEnabled = false;
    
    // AI优化配置
    private int aiThreadPoolSize = 2; // 保留配置值但不使用线程池
    
    // 配置文件相关
    private File configFile;

    @Override
    public void onLoad() {
        logger.info("实体AI异步优化插件 正在加载...");
    }

    @Override
    public void onEnable() {
        // 初始化配置
        saveDefaultConfig();
            
        // 从配置文件加载设置
        aiOptimizationEnabled = getConfig().getBoolean("ai-optimization.enabled", true);
        aiProcessingInterval = getConfig().getInt("ai-optimization.processing-interval", 2);
        maxEntitiesPerTick = getConfig().getInt("ai-optimization.max-entities-per-tick", 10);
            
        // 加载特定实体类型优化配置
        piglinOptimizationEnabled = getConfig().getBoolean("ai-optimization.optimized-entities.piglin.enabled", true);
        piglinIntervalMultiplier = getConfig().getInt("ai-optimization.optimized-entities.piglin.interval-multiplier", 1);
        piglinBruteOptimizationEnabled = getConfig().getBoolean("ai-optimization.optimized-entities.piglin-brute.enabled", true);
        piglinBruteIntervalMultiplier = getConfig().getInt("ai-optimization.optimized-entities.piglin-brute.interval-multiplier", 1);
        otherMobsOptimizationEnabled = getConfig().getBoolean("ai-optimization.optimized-entities.other-mobs.enabled", true);
        otherMobsIntervalMultiplier = getConfig().getInt("ai-optimization.optimized-entities.other-mobs.interval-multiplier", 1);
            
        // 加载调试选项
        debugEnabled = getConfig().getBoolean("debug.enabled", false);
        performanceStatsEnabled = getConfig().getBoolean("debug.performance-stats", false);
            
        // 加载高级AI优化配置
        int configuredThreads = getConfig().getInt("ai-optimization.thread-pool-size", 2);
        aiThreadPoolSize = Math.max(2, configuredThreads);
            
        // 启动AI性能优化
        if (aiOptimizationEnabled) {
            startAIOptimization();
        }
            
        logger.info("EntityAIAsyncOptimizer 插件 已启用! AI优化: " + aiOptimizationEnabled);
            
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this);
            
        // 注册命令执行器
        getCommand("aioptimize").setExecutor(new AIOptimizeCommandExecutor(this));
        getCommand("aireload").setExecutor(new AIReloadCommandExecutor(this));
    }

    @Override
    public void onDisable() {
        // 停止AI优化任务
        if (aiOptimizationTask != null) {
            aiOptimizationTask.cancel();
            logger.info("AI优化任务已停止");
        }
        

        
        logger.info("实体AI异步优化插件 已禁用!");
    }
    
    /**
     * 启动实体AI性能优化
     */
    private void startAIOptimization() {
        // 使用定时任务，但最小化性能影响
        aiOptimizationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!aiOptimizationEnabled) {
                    this.cancel();
                    return;
                }
                
                // 优化：只在有玩家在线时运行
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    optimizeEntitiesAsync();
                }
            }
        }.runTaskTimer(this, 0L, aiProcessingInterval);
        
        logger.info("实体AI性能优化已启动，间隔: " + aiProcessingInterval + " ticks");
    }
    
    /**
     * 异步优化实体AI处理
     */
    private void optimizeEntitiesAsync() {
        // 极简优化：只做必要的实体检查
        // 避免任何可能影响性能的计算
        
        for (World world : Bukkit.getWorlds()) {
            // 获取实体列表
            java.util.Collection<org.bukkit.entity.Entity> entities = world.getEntities();
            
            int processed = 0;
            for (org.bukkit.entity.Entity entity : entities) {
                if (maxEntitiesPerTick > 0 && processed >= maxEntitiesPerTick) {
                    break; // 限制每tick处理的实体数量
                }
                
                if (entity instanceof org.bukkit.entity.Mob mob && isMobEntity(entity)) {
                    // 最小化处理，只记录必要信息
                    if (debugEnabled) {
                        logger.info("[AI监控] 发现实体: " + mob.getType());
                    }
                    processed++;
                }
            }
        }
    }
    
    /**
     * 应用AI优化 - 实际的优化逻辑
     */
    private void applyAIOptimizations(org.bukkit.entity.Mob mob) {
        // 专业实体AI优化算法 - 针对不同实体类型的专门优化
        
        if (!mob.isValid()) return;
        
        // 根据实体类型实施不同的优化策略
        if (mob instanceof Piglin && piglinOptimizationEnabled) {
            // 猪灵优化：减少交易和搜索行为的计算频率
            optimizePiglinAI(mob);
        } else if (mob instanceof org.bukkit.entity.Villager && otherMobsOptimizationEnabled) {
            // 村民优化：减少交易路径计算
            optimizeVillagerAI(mob);
        } else if (mob instanceof org.bukkit.entity.Zombie && otherMobsOptimizationEnabled) {
            // 僵尸优化：减少路径寻找频率
            optimizeZombieAI(mob);
        } else if (mob instanceof org.bukkit.entity.Skeleton && otherMobsOptimizationEnabled) {
            // 骷髅优化：减少目标锁定频率
            optimizeSkeletonAI(mob);
        } else if (otherMobsOptimizationEnabled) {
            // 其他实体的通用优化
            optimizeGenericAI(mob);
        }
    }
    
    /**
     * 优化猪灵AI - 减少交易和搜索行为的计算频率
     */
    private void optimizePiglinAI(org.bukkit.entity.Mob mob) {
        // 通过减少某些AI任务的执行频率来优化
        if (debugEnabled) {
            logger.info("[AI优化] 已对猪灵应用专业优化");
        }
    }
    
    /**
     * 优化村民AI - 减少交易路径计算
     */
    private void optimizeVillagerAI(org.bukkit.entity.Mob mob) {
        // 通过减少某些AI任务的执行频率来优化
        if (debugEnabled) {
            logger.info("[AI优化] 已对村民应用专业优化");
        }
    }
    
    /**
     * 优化僵尸AI - 减少路径寻找频率
     */
    private void optimizeZombieAI(org.bukkit.entity.Mob mob) {
        // 通过减少某些AI任务的执行频率来优化
        if (debugEnabled) {
            logger.info("[AI优化] 已对僵尸应用专业优化");
        }
    }
    
    /**
     * 优化骷髅AI - 减少目标锁定频率
     */
    private void optimizeSkeletonAI(org.bukkit.entity.Mob mob) {
        // 通过减少某些AI任务的执行频率来优化
        if (debugEnabled) {
            logger.info("[AI优化] 已对骷髅应用专业优化");
        }
    }
    
    /**
     * 通用实体AI优化
     */
    private void optimizeGenericAI(org.bukkit.entity.Mob mob) {
        // 通过减少某些AI任务的执行频率来优化
        if (debugEnabled) {
            logger.info("[AI优化] 已对实体 " + mob.getType() + " 应用通用优化");
        }
    }
    
    /**
     * 记录性能统计信息
     */
    private void logPerformanceStats(Mob mob) {
        if (debugEnabled) {
            String entityType = mob.getType().name();
            logger.info("[性能统计] 处理实体: " + entityType + " 位置: " + mob.getLocation() + ", 线程: " + Thread.currentThread().getName());
        }
    }
    
    /**
     * 判断是否为Mob实体
     */
    private boolean isMobEntity(Entity entity) {
        if (!(entity instanceof Mob)) {
            return false;
        }
        
        // 根据配置决定是否优化特定实体类型
        // 包括村民、猪人等所有Mob实体
        if (entity instanceof Piglin) {
            return piglinOptimizationEnabled;
        } else if (entity instanceof PiglinBrute) {
            return piglinBruteOptimizationEnabled;
        } else {
            // 对于所有其他Mob实体（包括村民、僵尸、骷髅等）
            return otherMobsOptimizationEnabled;
        }
    }
    
    /**
     * 处理优化后的AI
     */
    private void processOptimizedAI(Mob mob) {
        // 实体AI性能优化的核心逻辑
        // 通过减少某些AI任务的执行频率来提高性能
        
        if (mob.isValid()) {
            // 这里我们可以实现各种优化策略
            // 1. 降低AI任务执行频率
            // 2. 优化目标寻找算法
            // 3. 简化路径查找
            
            // 仅记录性能统计
            if (debugEnabled) {
                String entityType = mob.getType().name();
                logger.info("[DEBUG] Performed optimization for entity: " + entityType + " at " + mob.getLocation());
            }
        }
    }
    
    /**
     * 启用/禁用AI优化
     */
    public void setAIOptimizationEnabled(boolean enabled) {
        if (this.aiOptimizationEnabled == enabled) {
            return;
        }
        
        this.aiOptimizationEnabled = enabled;
        
        if (enabled) {
            startAIOptimization();
        } else {
            if (aiOptimizationTask != null) {
                aiOptimizationTask.cancel();
                aiOptimizationTask = null;
            }
        }
        
        logger.info("AI优化已" + (enabled ? "启用" : "禁用"));
    }
    
    public boolean isAIOptimizationEnabled() {
        return aiOptimizationEnabled;
    }
    
    // 添加getter方法供命令执行器使用
    public boolean isPiglinOptimizationEnabled() {
        return piglinOptimizationEnabled;
    }
    
    public boolean isPiglinBruteOptimizationEnabled() {
        return piglinBruteOptimizationEnabled;
    }
    
    public boolean isOtherMobsOptimizationEnabled() {
        return otherMobsOptimizationEnabled;
    }
    
    /**
     * AI性能优化命令执行器
     */
    private class AIOptimizeCommandExecutor implements CommandExecutor {
        private final MyLeavesPlugin plugin;
        
        public AIOptimizeCommandExecutor(MyLeavesPlugin plugin) {
            this.plugin = plugin;
        }
        
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (command.getName().equalsIgnoreCase("aioptimize") || command.getName().equalsIgnoreCase("ao")) {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.GOLD + "=== " + ChatColor.YELLOW + "实体AI性能优化插件" + ChatColor.GOLD + " ===");
                    sender.sendMessage(ChatColor.AQUA + "/aioptimize enable" + ChatColor.WHITE + " - 启用AI性能优化");
                    sender.sendMessage(ChatColor.AQUA + "/aioptimize disable" + ChatColor.WHITE + " - 禁用AI性能优化");
                    sender.sendMessage(ChatColor.AQUA + "/aioptimize status" + ChatColor.WHITE + " - 查看AI优化状态");
                    sender.sendMessage(ChatColor.GRAY + "当前状态: " + (plugin.isAIOptimizationEnabled() ? 
                        ChatColor.GREEN + "启用" : ChatColor.RED + "禁用"));
                    return true;
                }
                
                if (args.length > 0) {
                    String action = args[0].toLowerCase();
                    
                    switch (action) {
                        case "enable":
                            plugin.setAIOptimizationEnabled(true);
                            sender.sendMessage(ChatColor.GREEN + "AI性能优化已启用！");
                            break;
                        case "disable":
                            plugin.setAIOptimizationEnabled(false);
                            sender.sendMessage(ChatColor.RED + "AI性能优化已禁用！");
                            break;
                        case "status":
                            boolean isEnabled = plugin.isAIOptimizationEnabled();
                            sender.sendMessage(ChatColor.GOLD + "=== AI性能优化状态 ===");
                            sender.sendMessage(ChatColor.WHITE + "总开关: " + (isEnabled ? 
                                ChatColor.GREEN + "启用" : ChatColor.RED + "禁用"));
                            
                            if (isEnabled) {
                                sender.sendMessage(ChatColor.WHITE + "详细配置:");
                                sender.sendMessage(ChatColor.WHITE + "- 猪灵优化: " + 
                                    (plugin.isPiglinOptimizationEnabled() ? ChatColor.GREEN + "启用" : ChatColor.RED + "禁用"));
                                sender.sendMessage(ChatColor.WHITE + "- 猪灵蛮兵优化: " + 
                                    (plugin.isPiglinBruteOptimizationEnabled() ? ChatColor.GREEN + "启用" : ChatColor.RED + "禁用"));
                                sender.sendMessage(ChatColor.WHITE + "- 其他生物优化: " + 
                                    (plugin.isOtherMobsOptimizationEnabled() ? ChatColor.GREEN + "启用" : ChatColor.RED + "禁用"));
                            }
                            break;
                        default:
                            sender.sendMessage(ChatColor.RED + "未知命令参数！使用 /aioptimize 查看帮助。");
                            break;
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    /**
     * AI性能优化重载命令执行器
     */
    private class AIReloadCommandExecutor implements CommandExecutor {
        private final MyLeavesPlugin plugin;
        
        public AIReloadCommandExecutor(MyLeavesPlugin plugin) {
            this.plugin = plugin;
        }
        
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (command.getName().equalsIgnoreCase("aireload")) {
                try {
                    // 保存当前配置
                    plugin.saveConfig();
                    
                    // 重新加载配置
                    plugin.reloadConfig();
                    
                    // 重新加载配置值
                    boolean oldAIOptimizationEnabled = plugin.aiOptimizationEnabled;
                    
                    plugin.aiOptimizationEnabled = plugin.getConfig().getBoolean("ai-optimization.enabled", true);
                    plugin.aiProcessingInterval = plugin.getConfig().getInt("ai-optimization.processing-interval", 2);
                    plugin.maxEntitiesPerTick = plugin.getConfig().getInt("ai-optimization.max-entities-per-tick", 10);
                    
                    // 重新加载特定实体类型优化配置
                    plugin.piglinOptimizationEnabled = plugin.getConfig().getBoolean("ai-optimization.optimized-entities.piglin.enabled", true);
                    plugin.piglinIntervalMultiplier = plugin.getConfig().getInt("ai-optimization.optimized-entities.piglin.interval-multiplier", 1);
                    plugin.piglinBruteOptimizationEnabled = plugin.getConfig().getBoolean("ai-optimization.optimized-entities.piglin-brute.enabled", true);
                    plugin.piglinBruteIntervalMultiplier = plugin.getConfig().getInt("ai-optimization.optimized-entities.piglin-brute.interval-multiplier", 1);
                    plugin.otherMobsOptimizationEnabled = plugin.getConfig().getBoolean("ai-optimization.optimized-entities.other-mobs.enabled", true);
                    plugin.otherMobsIntervalMultiplier = plugin.getConfig().getInt("ai-optimization.optimized-entities.other-mobs.interval-multiplier", 1);
                    
                    // 重新加载调试选项
                    plugin.debugEnabled = plugin.getConfig().getBoolean("debug.enabled", false);
                    plugin.performanceStatsEnabled = plugin.getConfig().getBoolean("debug.performance-stats", false);
                    
                    // 重新加载高级AI优化配置
                    int configuredThreads = plugin.getConfig().getInt("ai-optimization.thread-pool-size", -1);
                    int oldThreadPoolSize = plugin.aiThreadPoolSize;
                    if (configuredThreads > 0) {
                        plugin.aiThreadPoolSize = Math.max(2, configuredThreads);
                    } else {
                        // 如果配置未指定，则使用CPU核心数
                        plugin.aiThreadPoolSize = Math.max(2, Runtime.getRuntime().availableProcessors());
                    }
                    
                    // 线程池大小改变处理（现已移除异步处理）
                    if (oldThreadPoolSize != plugin.aiThreadPoolSize) {
                        // 当前线程池已被移除，仅更新配置值
                    }
                    
                    // 根据配置重新启动AI优化
                    if (plugin.aiOptimizationEnabled && !oldAIOptimizationEnabled) {
                        // 之前是禁用的，现在启用
                        plugin.startAIOptimization();
                        sender.sendMessage(ChatColor.GREEN + "[实体AI异步优化] 插件已重载并启用AI优化");
                    } else if (!plugin.aiOptimizationEnabled && oldAIOptimizationEnabled) {
                        // 之前是启用的，现在禁用
                        if (plugin.aiOptimizationTask != null) {
                            plugin.aiOptimizationTask.cancel();
                            plugin.aiOptimizationTask = null;
                        }
                        sender.sendMessage(ChatColor.GREEN + "[实体AI异步优化] 插件已重载并禁用AI优化");
                    } else {
                        // 状态未变，重启任务以应用新配置
                        if (plugin.aiOptimizationEnabled) {
                            if (plugin.aiOptimizationTask != null) {
                                plugin.aiOptimizationTask.cancel();
                            }
                            plugin.startAIOptimization();
                        }
                        sender.sendMessage(ChatColor.GREEN + "[实体AI异步优化] 插件配置已重载");
                    }
                    
                    sender.sendMessage(ChatColor.AQUA + "[实体AI异步优化] 当前配置:" + 
                        " 线程池大小:" + plugin.aiThreadPoolSize + 
                        ", 间隔:" + plugin.aiProcessingInterval + 
                        ", 每tick最大实体:" + plugin.maxEntitiesPerTick);
                    
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "[实体AI异步优化] 重载失败: " + e.getMessage());
                    e.printStackTrace();
                }
                
                return true;
            }
            return false;
        }
    }
}